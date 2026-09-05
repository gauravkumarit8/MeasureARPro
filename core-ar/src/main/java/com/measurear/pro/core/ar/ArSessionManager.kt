package com.measurear.pro.core.ar

/**
 * Wraps ARCore session lifecycle + plane detection. Concrete ARCore/SceneView
 * calls are Phase 1 work — this Phase 0 stub defines the contract feature
 * modules code against, so :feature-measure etc. can be built in parallel.
 */
interface ArSessionManager {
    fun isArSupported(context: android.content.Context): Boolean
    fun startSession()
    fun stopSession()
    fun placePoint(screenX: Float, screenY: Float): ArPoint?
}

data class ArPoint(val x: Float, val y: Float, val z: Float)

fun distanceBetween(a: ArPoint, b: ArPoint): Double {
    val dx = (a.x - b.x).toDouble()
    val dy = (a.y - b.y).toDouble()
    val dz = (a.z - b.z).toDouble()
    return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
}
