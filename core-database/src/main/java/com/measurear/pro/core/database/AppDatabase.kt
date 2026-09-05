package com.measurear.pro.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.measurear.pro.core.database.entities.FurniturePresetEntity
import com.measurear.pro.core.database.entities.MeasurementEntity
import com.measurear.pro.core.database.entities.RoomPlanEntity

@Database(
    entities = [MeasurementEntity::class, RoomPlanEntity::class, FurniturePresetEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    // Phase 1: add abstract DAO accessors (MeasurementDao, RoomPlanDao, FurniturePresetDao)
    // as each feature module needs them, rather than defining all DAOs up front.
}
