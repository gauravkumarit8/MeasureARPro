package com.measurear.pro.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * PRD Section 3.1/3.2: free tier caps stored history at 5 (enforced in the
 * repository layer, not the schema); Pro tier is unlimited with folder/tag grouping.
 */
@Entity(tableName = "measurements")
data class MeasurementEntity(
    @PrimaryKey val id: String,
    val distanceMeters: Double,
    val confidenceSpreadCm: Double,
    val confidenceLevel: String,
    val timestampEpochMillis: Long,
    val photoUri: String?,
    val folderId: String? = null // Pro-only organization; null for free-tier entries
)
