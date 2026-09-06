package com.measurear.pro.feature.measure

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.ar.core.Config
import com.measurear.pro.core.ar.ArAvailability
import com.measurear.pro.core.ar.ArSessionManager
import com.measurear.pro.core.database.DatabaseProvider
import com.measurear.pro.core.database.repository.RoomMeasurementRepository
import io.github.sceneview.ar.ARScene
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberOnGestureListener

/**
 * PRD Wireframe 1: default landing screen. Handles, in order:
 *  1. Runtime CAMERA permission (contextual, with rationale — Play review
 *     expects this, not an eager request on app launch).
 *  2. ARCore availability + install-or-update flow.
 *  3. The live AR camera feed + measuring flow, via ArSceneContent below.
 */
@Composable
fun MeasureScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var showRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    when {
        !hasCameraPermission -> CameraPermissionGate(
            showRationale = showRationale,
            onRequest = {
                showRationale = false
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onExplain = { showRationale = true }
        )
        activity == null -> Text("AR measuring requires an Activity context")
        else -> ArMeasureContent(activity)
    }
}

@Composable
private fun CameraPermissionGate(showRationale: Boolean, onRequest: () -> Unit, onExplain: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("MeasureAR Pro needs camera access to measure distances using AR.")
        if (showRationale) {
            Text("We only use the camera live, on-screen, for AR measuring \u2014 no photos are taken unless you explicitly save a measurement.")
        }
        Button(onClick = { onExplain(); onRequest() }) {
            Text("Grant Camera Access")
        }
    }
}

@Composable
private fun ArMeasureContent(activity: Activity) {
    val arSessionManager = remember { ArSessionManager() }
    val repository = remember {
        RoomMeasurementRepository(DatabaseProvider.getDatabase(activity).measurementDao())
    }
    val viewModel = remember { MeasureViewModel(arSessionManager, repository) }
    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }
    val uiState by viewModel.uiState.collectAsState()
    var availability by remember { mutableStateOf<ArAvailability?>(null) }

    LaunchedEffect(Unit) {
        // Only the availability/install check happens here — SceneView's
        // ARScene composable (below) creates and owns the actual ARCore
        // Session itself once availability is confirmed Ready. Calling
        // ArSessionManager.createSession()/resume() here too would create a
        // second, conflicting Session.
        availability = arSessionManager.resolveAvailability(activity, userRequestedInstall = true)
    }

    when (val state = availability) {
        null -> Text("Checking AR availability\u2026")
        ArAvailability.InstallRequested -> Text(
            "Finish installing \u2018Google Play Services for AR\u2019, then come back to this screen."
        )
        ArAvailability.UnsupportedDevice -> UnsupportedDeviceFallback()
        is ArAvailability.Error -> Text("AR unavailable: ${state.message}. Falling back to Screen Ruler is recommended.")
        ArAvailability.Ready -> ArSceneContent(arSessionManager, viewModel, uiState)
    }
}

/**
 * Live AR camera feed + plane visualization, via SceneView's ARScene composable
 * (io.github.sceneview:arsceneview). Confirmed against SceneView's own
 * published README example: engine + modelLoader, session config, frame feed
 * into ArSessionManager, and tap gesture all compile and work correctly.
 *
 * 3D marker rendering at placed points was attempted here (SphereNode +
 * AnchorNode + childNodes) but pulled back out after two failed compile
 * attempts against partial/unconverged evidence for our pinned 2.2.1
 * version — rather than keep guessing at an unconfirmed API a third time,
 * this is deferred as a real follow-up task: check SceneView's actual sample
 * app source for version 2.2.x specifically (not README snippets spanning
 * multiple major versions) before re-attempting. The measuring flow is fully
 * functional without it — users get a correct, confidence-scored distance
 * readout, just no visual dot at the tapped points yet.
 */
@Composable
private fun ArSceneContent(
    arSessionManager: ArSessionManager,
    viewModel: MeasureViewModel,
    uiState: MeasureUiState
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            planeRenderer = true,
            sessionConfiguration = { session, config ->
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                config.lightEstimationMode = Config.LightEstimationMode.DISABLED
                config.depthMode = if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    Config.DepthMode.AUTOMATIC
                } else {
                    Config.DepthMode.DISABLED
                }
            },
            onSessionUpdated = { _, updatedFrame ->
                // Feeds ArSessionManager's hit-testing — see placePoint().
                arSessionManager.onNewFrame(updatedFrame)
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { motionEvent, _ ->
                    viewModel.onTapAt(motionEvent.x, motionEvent.y)
                }
            )
        )

        // Readout overlay — sits on top of the live camera feed.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(uiState.readoutText, color = androidx.compose.ui.graphics.Color.White)
            if (uiState.step == MeasureStep.DONE) {
                Button(onClick = { viewModel.resetForNewMeasurement() }) {
                    Text("Measure Again")
                }
            }
        }
    }
}

@Composable
private fun UnsupportedDeviceFallback() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("This device doesn't support AR measuring.")
        Text("Use the Ruler tab instead \u2014 it works on every device.")
    }
}
