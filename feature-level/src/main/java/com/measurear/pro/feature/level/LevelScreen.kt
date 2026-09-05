package com.measurear.pro.feature.level

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * PRD Section 3.1: accelerometer-based horizontal/vertical leveling.
 * Phase 1: read SensorManager TYPE_ACCELEROMETER, render a bubble indicator
 * that centers at 0deg tilt, show degree readout.
 */
@Composable
fun LevelScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("[ Bubble Level placeholder \u2014 0.0\u00b0 ]")
    }
}
