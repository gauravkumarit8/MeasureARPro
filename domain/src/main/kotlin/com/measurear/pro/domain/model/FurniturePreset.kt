package com.measurear.pro.domain.model

data class FurniturePreset(
    val id: String,
    val label: String,
    val widthCm: Float,
    val depthCm: Float,
    val heightCm: Float,
    val isUserSaved: Boolean = false
)
