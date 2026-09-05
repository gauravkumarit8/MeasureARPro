package com.measurear.pro.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.measurear.pro.feature.converter.ConverterScreen
import com.measurear.pro.feature.fitchecker.FitCheckerScreen
import com.measurear.pro.feature.level.LevelScreen
import com.measurear.pro.feature.measure.MeasureScreen
import com.measurear.pro.feature.paywall.PaywallScreen
import com.measurear.pro.feature.roomplan.RoomPlanScreen
import com.measurear.pro.feature.ruler.RulerScreen
import com.measurear.pro.feature.templates.TemplatesScreen

/**
 * Destinations mirror the bottom tab bar in PRD Wireframe 1 / Wireframe 4 paywall modal.
 * RoomPlan and FitChecker (full) are Pro-gated — each screen checks EntitlementState
 * internally (see :core-billing) and routes to Paywall if not entitled, rather than
 * hiding the tab entirely, so free users can see what they're missing.
 */
sealed class Destination(val route: String) {
    data object Distance : Destination("distance")
    data object RoomPlan : Destination("roomplan")
    data object FitChecker : Destination("fitchecker")
    data object Level : Destination("level")
    data object Ruler : Destination("ruler")
    data object Converter : Destination("converter")
    data object Templates : Destination("templates")
    data object Paywall : Destination("paywall")
}

@Composable
fun MeasureARProNavHost(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { MeasureARProBottomBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            NavHost(navController = navController, startDestination = Destination.Distance.route) {
                composable(Destination.Distance.route) { MeasureScreen() }
                composable(Destination.RoomPlan.route) { RoomPlanScreen() }
                composable(Destination.FitChecker.route) { FitCheckerScreen() }
                composable(Destination.Level.route) { LevelScreen() }
                composable(Destination.Ruler.route) { RulerScreen() }
                composable(Destination.Converter.route) { ConverterScreen() }
                composable(Destination.Templates.route) { TemplatesScreen() }
                composable(Destination.Paywall.route) { PaywallScreen() }
            }
        }
    }
}
