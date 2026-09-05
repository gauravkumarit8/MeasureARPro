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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.measurear.pro.core.ar.ArAvailability
import com.measurear.pro.core.ar.ArSessionManager

/**
 * PRD Wireframe 1: default landing screen. Handles, in order:
 *  1. Runtime CAMERA permission (contextual, with rationale — Play review
 *     expects this, not an eager request on app launch).
 *  2. ARCore availability + install-or-update flow.
 *  3. The AR measuring flow itself, delegated to MeasureViewModel.
 *
 * The actual camera feed + plane visualization renders via SceneView's ARScene
 * composable (io.github.sceneview:arsceneview). SceneView's exact composable
 * parameter names have changed across versions — verify against the pinned
 * version in gradle/libs.versions.toml when this first compiles; the shape
 * below (onSessionCreated / onFrame / tap handling) reflects the library's
 * documented pattern at the time this was written, not a guarantee for every
 * release.
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
    val viewModel = remember { MeasureViewModel(arSessionManager) }
    val uiState by viewModel.uiState.collectAsState()
    var availability by remember { mutableStateOf<ArAvailability?>(null) }

    LaunchedEffect(Unit) {
        availability = arSessionManager.resolveAvailability(activity, userRequestedInstall = true)
        if (availability == ArAvailability.Ready) {
            arSessionManager.createSession(activity)
            arSessionManager.resume()
        }
    }

    when (val state = availability) {
        null -> Text("Checking AR availability\u2026")
        ArAvailability.InstallRequested -> Text(
            "Finish installing \u2018Google Play Services for AR\u2019, then come back to this screen."
        )
        ArAvailability.UnsupportedDevice -> UnsupportedDeviceFallback()
        is ArAvailability.Error -> Text("AR unavailable: ${state.message}. Falling back to Screen Ruler is recommended.")
        ArAvailability.Ready -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset -> viewModel.onTapAt(offset.x, offset.y) }
                    }
            ) {
                // TODO Phase 1 follow-up: replace with SceneView's ARScene composable
                // for the live camera feed + plane overlay, wired to
                // arSessionManager.onNewFrame(frame) on every ARCore frame update.
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("[ AR camera view \u2014 SceneView ARScene goes here ]")
                    Text(uiState.readoutText)
                    if (uiState.step == MeasureStep.DONE) {
                        Button(onClick = { viewModel.resetForNewMeasurement() }) {
                            Text("Measure Again")
                        }
                    }
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

// NOTE: once SceneView's ARScene owns pointer input for camera gestures
// (pan/zoom on the AR view), replace the raw pointerInput/detectTapGestures
// block above with SceneView's own onTap callback, to avoid two gesture
// handlers competing for the same touch events.
