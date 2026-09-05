package com.measurear.pro.feature.roomplan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * PRD Wireframe 3 / Section 3.2 (Pro): multi-point wall capture, auto perimeter/
 * area/ceiling-height, doors/windows, cost estimate line, Save/Export/Share-via-QR.
 * Phase 3 work. This screen must check EntitlementState (:core-billing) on entry
 * and route to Paywall if the user isn't Pro, per PRD Section 4.
 */
@Composable
fun RoomPlanScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("[ Room Planner placeholder \u2014 Pro feature ]")
        Text("Perimeter: -- m   Area: -- m\u00b2")
        Button(onClick = { /* Phase 3: entitlement check -> Paywall or capture flow */ }) {
            Text("Start Room Capture")
        }
    }
}
