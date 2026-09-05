package com.measurear.pro.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.measurear.pro.core.database.dao.MeasurementDao
import com.measurear.pro.core.database.entities.FurniturePresetEntity
import com.measurear.pro.core.database.entities.MeasurementEntity
import com.measurear.pro.core.database.entities.RoomPlanEntity

@Database(
    entities = [MeasurementEntity::class, RoomPlanEntity::class, FurniturePresetEntity::class],
    version = 1,
    // false for now — no migration testing infra set up yet. Switching this to
    // true later requires configuring room.schemaLocation via the Room Gradle
    // plugin (id("androidx.room")); the ksp-arg-only approach the compiler
    // warning suggests is deprecated. Revisit once Phase 3+ needs real
    // migrations (e.g. adding the folder/tag column for Pro organization).
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao
    // Phase 3/4: add roomPlanDao() and furniturePresetDao() when those features
    // actually need persistence — no point defining unused DAOs speculatively.
}
