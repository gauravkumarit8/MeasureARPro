package com.measurear.pro.core.ar

import android.app.Activity
import android.content.Context
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException

data class ArPoint(val x: Float, val y: Float, val z: Float)

/** Point position plus the ARCore Anchor it's attached to — the anchor is what
 *  lets a visual marker node stay fixed in world space as the camera moves.
 *  Caller (ViewModel) is responsible for calling anchor.detach() when the
 *  point is no longer needed (measurement reset), or it leaks tracking resources. */
data class PlacedPoint(val position: ArPoint, val anchor: com.google.ar.core.Anchor)

fun distanceBetween(a: ArPoint, b: ArPoint): Double {
    val dx = (a.x - b.x).toDouble()
    val dy = (a.y - b.y).toDouble()
    val dz = (a.z - b.z).toDouble()
    return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
}

/** Result of asking whether/how to proceed with AR on this device+moment. */
sealed class ArAvailability {
    data object Ready : ArAvailability()
    /** Play Store install/update of "Google Play Services for AR" was triggered;
     *  caller should wait for onResume and re-check rather than proceed. */
    data object InstallRequested : ArAvailability()
    /** Device can never support ARCore — caller should route to Screen Ruler
     *  (PRD Section 6 fallback) and not offer the AR tab again this session. */
    data object UnsupportedDevice : ArAvailability()
    data class Error(val message: String) : ArAvailability()
}

/**
 * Wraps ARCore availability/install-flow checking and hit-testing math.
 *
 * IMPORTANT: when rendering via SceneView's ARScene composable (as
 * MeasureScreen does), SceneView creates and owns its own ARCore Session
 * internally — createSession()/resume()/pause() below are NOT used in that
 * path and would conflict with SceneView's session if called alongside it.
 * They're kept for a hypothetical future custom-renderer path that doesn't
 * go through SceneView. The methods actually used with SceneView are:
 *   1. resolveAvailability(activity) — before showing ARScene at all
 *   2. onNewFrame(frame) — fed from ARScene's onSessionUpdated callback
 *   3. placePoint(x, y) — called from the tap gesture callback
 */
class ArSessionManager {

    private var session: Session? = null
    private var latestFrame: Frame? = null

    /**
     * Must be called from Activity.onResume, per ARCore's documented lifecycle
     * contract. requestInstall's second argument (userRequestedInstall) should
     * be true only the first time in a given user flow, false on the automatic
     * re-check after returning from the Play Store install screen.
     */
    fun resolveAvailability(activity: Activity, userRequestedInstall: Boolean): ArAvailability {
        val availability = ArCoreApk.getInstance().checkAvailability(activity)
        if (availability.isTransient) {
            // Availability check is still resolving (e.g. network call in flight) —
            // caller should poll again shortly rather than treat this as failure.
            return ArAvailability.Error("AR availability check in progress, retry shortly")
        }
        return when {
            availability.isSupported -> ArAvailability.Ready
            availability == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE ->
                ArAvailability.UnsupportedDevice
            else -> try {
                when (ArCoreApk.getInstance().requestInstall(activity, userRequestedInstall)) {
                    ArCoreApk.InstallStatus.INSTALLED -> ArAvailability.Ready
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> ArAvailability.InstallRequested
                    else -> ArAvailability.Error("Unknown ARCore install status")
                }
            } catch (e: UnavailableDeviceNotCompatibleException) {
                ArAvailability.UnsupportedDevice
            } catch (e: UnavailableUserDeclinedInstallationException) {
                ArAvailability.Error("User declined ARCore installation")
            } catch (e: Exception) {
                ArAvailability.Error(e.message ?: "ARCore install request failed")
            }
        }
    }

    private val ArCoreApk.Availability.isSupported: Boolean
        get() = this == ArCoreApk.Availability.SUPPORTED_INSTALLED

    private val ArCoreApk.Availability.isTransient: Boolean
        get() = this == ArCoreApk.Availability.UNKNOWN_CHECKING

    /** Call after resolveAvailability() returns Ready, once per AR screen entry.
     *  NOT used on the SceneView path — see class-level doc comment above. */
    @Throws(
        UnavailableApkTooOldException::class,
        UnavailableSdkTooOldException::class,
        UnavailableDeviceNotCompatibleException::class
    )
    fun createSession(context: Context) {
        val newSession = Session(context)
        val config = Config(newSession).apply {
            planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
            lightEstimationMode = Config.LightEstimationMode.DISABLED
            focusMode = Config.FocusMode.AUTO
            // Depth API improves hit-test accuracy on supported devices; falls
            // back gracefully on devices without a depth sensor.
            depthMode = if (newSession.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                Config.DepthMode.AUTOMATIC
            } else {
                Config.DepthMode.DISABLED
            }
        }
        newSession.configure(config)
        session = newSession
    }

    fun resume() = session?.resume()
    fun pause() = session?.pause()

    fun onNewFrame(frame: Frame) {
        latestFrame = frame
    }

    /**
     * Hit-tests the given screen coordinate against detected planes/points in
     * the most recent frame, and creates an anchor at the hit so a visual
     * marker can be attached (see PlacedPoint doc). Returns null if tracking
     * isn't stable yet or no surface was hit — caller should prompt the user
     * to keep scanning.
     */
    fun placePoint(screenX: Float, screenY: Float): PlacedPoint? {
        val frame = latestFrame ?: return null
        if (frame.camera.trackingState != TrackingState.TRACKING) return null

        val hitResults = frame.hitTest(screenX, screenY)
        val best = hitResults.firstOrNull { hit ->
            val trackable = hit.trackable
            (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) ||
                trackable is com.google.ar.core.Point
        } ?: return null

        val anchor = best.createAnchor()
        val pose = anchor.pose
        return PlacedPoint(ArPoint(pose.tx(), pose.ty(), pose.tz()), anchor)
    }

    fun close() {
        session?.close()
        session = null
        latestFrame = null
    }
}
