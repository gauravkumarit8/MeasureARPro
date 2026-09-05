package com.measurear.pro.feature.ruler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * PRD Section 3.1: on-screen ruler + one-time card-alignment calibration step
 * (corrects for devices that misreport PPI). Also the ARCore fallback for
 * devices where ArSessionManager.isArSupported() returns false.
 */
@Composable
fun RulerScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("[ Screen Ruler placeholder ]")
        Button(onClick = { /* Phase 1: launch card-alignment calibration flow */ }) {
            Text("Calibrate")
        }
    }
}
