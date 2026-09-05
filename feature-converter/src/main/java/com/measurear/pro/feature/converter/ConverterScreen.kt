package com.measurear.pro.feature.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * PRD Section 3.1: length + area units only (mm/cm/m/km, in/ft/yd/mile, sq ft/sq m)
 * — intentionally scoped to the measuring use case, not a general-purpose converter.
 */
@Composable
fun ConverterScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("[ Unit Converter placeholder \u2014 length & area only ]")
    }
}
