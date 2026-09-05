package com.measurear.pro.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

private val tabs = listOf(
    Destination.Distance to "Distance",
    Destination.RoomPlan to "Room Plan",
    Destination.FitChecker to "Fit",
    Destination.Level to "Level",
    Destination.Ruler to "Ruler"
    // Converter + Templates live under a "More" overflow per Wireframe 1 — add once
    // a real overflow menu component exists; omitted from the bottom bar in Phase 0.
)

@Composable
fun MeasureARProBottomBar(navController: NavHostController) {
    NavigationBar {
        tabs.forEach { (destination, label) ->
            NavigationBarItem(
                selected = false, // Phase 0 stub — wire to currentBackStackEntry in Phase 1
                onClick = { navController.navigate(destination.route) },
                icon = {}, // TODO Phase 1: real icons per Wireframe 1
                label = { Text(label) }
            )
        }
    }
}
