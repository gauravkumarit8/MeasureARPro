package com.measurear.pro.feature.level

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt

private const val LEVEL_THRESHOLD_DEGREES = 0.5f

/**
 * PRD Section 3.1: accelerometer-based horizontal/vertical leveling with a
 * visual bubble indicator and degree readout. Uses a low-pass filter to smooth
 * raw accelerometer noise — without it the bubble jitters constantly even when
 * the phone is still.
 */
@Composable
fun LevelScreen() {
    val context = LocalContext.current
    var pitchDegrees by remember { mutableFloatStateOf(0f) }
    var rollDegrees by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gravity = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Low-pass filter: smooths jitter, alpha tuned for a responsive-but-stable bubble.
                val alpha = 0.15f
                for (i in 0..2) {
                    gravity[i] = alpha * event.values[i] + (1 - alpha) * gravity[i]
                }
                pitchDegrees = Math.toDegrees(atan2(gravity[1].toDouble(), gravity[2].toDouble())).toFloat()
                rollDegrees = Math.toDegrees(atan2(-gravity[0].toDouble(), gravity[2].toDouble())).toFloat()
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }
        onDispose { sensorManager.unregisterListener(listener) }
    }

    val isLevel = abs(pitchDegrees) < LEVEL_THRESHOLD_DEGREES && abs(rollDegrees) < LEVEL_THRESHOLD_DEGREES

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BubbleLevelIndicator(pitchDegrees, rollDegrees, isLevel)
        Text(
            text = "Pitch: ${pitchDegrees.roundToTenth()}\u00b0   Roll: ${rollDegrees.roundToTenth()}\u00b0",
            modifier = Modifier.padding(top = 16.dp)
        )
        if (isLevel) Text("\u2713 Level", color = Color(0xFF2E7D32))
    }
}

@Composable
private fun BubbleLevelIndicator(pitch: Float, roll: Float, isLevel: Boolean) {
    val bubbleColor = if (isLevel) Color(0xFF2E7D32) else Color(0xFFD32F2F)
    Box(modifier = Modifier.size(220.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f
            drawCircle(color = Color.LightGray, radius = radius, center = center, style = Stroke(width = 4f))
            // Clamp so the bubble stays inside the ring even at steep tilt angles.
            val clampedX = (roll / 45f).coerceIn(-1f, 1f) * (radius - 24f)
            val clampedY = (pitch / 45f).coerceIn(-1f, 1f) * (radius - 24f)
            drawCircle(
                color = bubbleColor,
                radius = 24f,
                center = Offset(center.x + clampedX, center.y + clampedY)
            )
        }
    }
}

private fun Float.roundToTenth(): Float = (this * 10).roundToInt() / 10f
