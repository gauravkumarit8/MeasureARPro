package com.measurear.pro.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_plans")
data class RoomPlanEntity(
    @PrimaryKey val id: String,
    val name: String,
    val wallsJson: String, // serialized List<WallSegment> — see :domain model
    val ceilingHeightMeters: Float,
    val doors: Int,
    val windows: Int,
    val createdAtEpochMillis: Long
)
