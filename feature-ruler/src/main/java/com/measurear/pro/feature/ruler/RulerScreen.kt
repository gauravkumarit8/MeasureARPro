package com.measurear.pro.feature.ruler

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** A standard ID-1 bank/credit card is 85.60mm wide — used as the calibration reference. */
private const val CARD_WIDTH_MM = 85.60f

/**
 * PRD Section 3.1: on-screen ruler calibrated via a manual card-alignment step
 * (corrects for devices that misreport PPI), and the ARCore fallback for
 * devices where ArSessionManager reports UnsupportedDevice.
 */
@Composable
fun RulerScreen() {
    var pixelsPerMm by remember { mutableStateOf<Float?>(null) }

    if (pixelsPerMm == null) {
        CalibrationStep(onCalibrated = { pixelsPerMm = it })
    } else {
        RulerView(pixelsPerMm = pixelsPerMm!!, onRecalibrate = { pixelsPerMm = null })
    }
}

@Composable
private fun CalibrationStep(onCalibrated: (Float) -> Unit) {
    // Widened/narrowed by the slider until the drawn rectangle matches a real
    // card's width; the resulting width in actual device pixels (not dp) is
    // what gets divided by the card's known physical width to get pixelsPerMm.
    var sliderWidthDp by remember { mutableFloatStateOf(300f) }
    val density = LocalDensity.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Calibrate: align a standard card (credit/debit/ID) against the bar below")
        Canvas(modifier = Modifier.fillMaxWidth().height(80.dp).padding(vertical = 16.dp)) {
            val widthPx = sliderWidthDp.dp.toPx()
            drawRect(
                color = Color(0xFF1F4E79),
                topLeft = Offset((size.width - widthPx) / 2f, size.height / 4f),
                size = androidx.compose.ui.geometry.Size(widthPx, size.height / 2f)
            )
        }
        Slider(
            value = sliderWidthDp,
            onValueChange = { sliderWidthDp = it },
            valueRange = 150f..450f
        )
        Button(onClick = {
            val widthPx = with(density) { sliderWidthDp.dp.toPx() }
            onCalibrated(widthPx / CARD_WIDTH_MM)
        }) {
            Text("Confirm Calibration")
        }
    }
}

@Composable
private fun RulerView(pixelsPerMm: Float, onRecalibrate: () -> Unit) {
    val textMeasurer = rememberTextMeasurer()
    Column(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp).padding(16.dp)) {
            val mmPerCm = 10
            val totalCm = (size.width / (pixelsPerMm * mmPerCm)).toInt()
            for (cm in 0..totalCm) {
                val x = cm * mmPerCm * pixelsPerMm
                drawLine(Color.Black, Offset(x, 0f), Offset(x, size.height * 0.5f), strokeWidth = 3f)
                drawText(
                    textMeasurer = textMeasurer,
                    text = "$cm",
                    topLeft = Offset(x + 4f, size.height * 0.5f),
                    style = TextStyle(fontSize = 12.sp)
                )
                for (mm in 1 until mmPerCm) {
                    val mmX = x + mm * pixelsPerMm
                    drawLine(Color.Gray, Offset(mmX, 0f), Offset(mmX, size.height * 0.25f), strokeWidth = 1.5f)
                }
            }
        }
        Button(onClick = onRecalibrate, modifier = Modifier.padding(16.dp)) {
            Text("Recalibrate")
        }
    }
}
