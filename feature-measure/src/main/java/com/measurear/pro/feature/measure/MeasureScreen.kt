package com.measurear.pro.feature.measure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * PRD Wireframe 1: default landing screen. Phase 0 stub renders a placeholder
 * camera-view box and a "Tap to set point" button; Phase 1 wires the real
 * ArSessionManager (:core-ar) plane detection + tap-to-place + ConfidenceScorer
 * 3-sample readout ("1.45 m \u00b10.5cm").
 */
@Composable
fun MeasureScreen(viewModel: MeasureViewModel = MeasureViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "[ AR camera view placeholder ]")
        Text(text = uiState.readoutText)
        Button(onClick = { viewModel.onTapSetPoint() }) {
            Text("TAP TO SET POINT")
        }
    }
}
