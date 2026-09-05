package com.measurear.pro.feature.fitchecker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * PRD Wireframe 2 / Section 3.1 (free basic) + 3.2 (Pro full):
 * type in W x D x H, place a virtual AR box anchored to a detected plane via
 * SceneView (:core-ar). Free tier: one active anchor, not saved. Pro tier:
 * multiple anchors + saved FurniturePreset library (:core-database).
 * Phase 4 work.
 */
@Composable
fun FitCheckerScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("[ Fit Checker placeholder ]")
        Text("W: -- cm   D: -- cm   H: -- cm")
        Button(onClick = { /* Phase 4: open dimension entry + place virtual box */ }) {
            Text("Edit Dimensions")
        }
    }
}
