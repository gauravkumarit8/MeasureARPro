package com.measurear.pro.feature.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * PRD Section 3.1 (free basic: Curtains, Mattress) + 3.2 (Pro full: Paint/
 * Wallpaper, Flooring/Tile, custom allowance %). Phase 5 work — pure formula
 * layer on top of existing measurement outputs, see domain.usecase.CostEstimator.
 */
@Composable
fun TemplatesScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("[ Guided Templates placeholder ]")
        Text("Curtains \u2022 Mattress \u2022 Paint (Pro) \u2022 Flooring (Pro)")
    }
}
