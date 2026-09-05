package com.measurear.pro.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Pro-tier Fit Checker saved object library — PRD Section 3.2. */
@Entity(tableName = "furniture_presets")
data class FurniturePresetEntity(
    @PrimaryKey val id: String,
    val label: String,
    val widthCm: Float,
    val depthCm: Float,
    val heightCm: Float
)
