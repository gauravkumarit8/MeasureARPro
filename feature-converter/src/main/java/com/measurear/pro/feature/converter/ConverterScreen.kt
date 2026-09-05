package com.measurear.pro.feature.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * PRD Section 3.1: length + area units only (mm/cm/m/km, in/ft/yd/mile,
 * sq ft/sq m) — intentionally scoped to the measuring use case, not a
 * general-purpose converter (currency etc. are Pro-tier, separate module).
 */
enum class LengthUnit(val label: String, val toMeters: Double) {
    MM("mm", 0.001), CM("cm", 0.01), M("m", 1.0), KM("km", 1000.0),
    IN("in", 0.0254), FT("ft", 0.3048), YD("yd", 0.9144), MILE("mile", 1609.344)
}

enum class AreaUnit(val label: String, val toSquareMeters: Double) {
    SQ_FT("sq ft", 0.09290304), SQ_M("sq m", 1.0)
}

fun convertLength(value: Double, from: LengthUnit, to: LengthUnit): Double =
    value * from.toMeters / to.toMeters

fun convertArea(value: Double, from: AreaUnit, to: AreaUnit): Double =
    value * from.toSquareMeters / to.toSquareMeters

@Composable
fun ConverterScreen() {
    var mode by remember { mutableStateOf("length") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row {
            TextButton(onClick = { mode = "length" }) { Text("Length") }
            TextButton(onClick = { mode = "area" }) { Text("Area") }
        }
        if (mode == "length") LengthConverter() else AreaConverter()
    }
}

@Composable
private fun LengthConverter() {
    var input by remember { mutableStateOf("1") }
    var fromUnit by remember { mutableStateOf(LengthUnit.M) }
    var toUnit by remember { mutableStateOf(LengthUnit.FT) }
    val result = input.toDoubleOrNull()?.let { convertLength(it, fromUnit, toUnit) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Value") })
        UnitDropdown(LengthUnit.entries, fromUnit, { fromUnit = it }, "From")
        UnitDropdown(LengthUnit.entries, toUnit, { toUnit = it }, "To")
        Text(result?.let { "= ${"%.4f".format(it)} ${toUnit.label}" } ?: "Enter a valid number")
    }
}

@Composable
private fun AreaConverter() {
    var input by remember { mutableStateOf("1") }
    var fromUnit by remember { mutableStateOf(AreaUnit.SQ_M) }
    var toUnit by remember { mutableStateOf(AreaUnit.SQ_FT) }
    val result = input.toDoubleOrNull()?.let { convertArea(it, fromUnit, toUnit) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Value") })
        UnitDropdown(AreaUnit.entries, fromUnit, { fromUnit = it }, "From")
        UnitDropdown(AreaUnit.entries, toUnit, { toUnit = it }, "To")
        Text(result?.let { "= ${"%.4f".format(it)} ${toUnit.label}" } ?: "Enter a valid number")
    }
}

@Composable
private fun <T> UnitDropdown(options: List<T>, selected: T, onSelect: (T) -> Unit, label: String) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        TextButton(onClick = { expanded = true }) {
            Text("$label: $selected")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option.toString()) }, onClick = {
                    onSelect(option)
                    expanded = false
                })
            }
        }
    }
}
