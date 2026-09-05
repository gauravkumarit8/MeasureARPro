package com.measurear.pro.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.measurear.pro.core.database.entities.MeasurementEntity

@Dao
interface MeasurementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(measurement: MeasurementEntity)

    @Query("SELECT * FROM measurements ORDER BY timestampEpochMillis DESC LIMIT :limit")
    suspend fun recent(limit: Int): List<MeasurementEntity>

    @Query("SELECT * FROM measurements ORDER BY timestampEpochMillis DESC")
    suspend fun all(): List<MeasurementEntity>

    @Query("SELECT COUNT(*) FROM measurements")
    suspend fun count(): Int

    // Deletes everything except the N most recent — used to enforce the
    // free-tier 5-item cap (PRD Section 3.1). Pro tier calls this with a very
    // high cap (effectively unlimited) rather than skipping the call.
    @Query(
        """
        DELETE FROM measurements
        WHERE id NOT IN (
            SELECT id FROM measurements ORDER BY timestampEpochMillis DESC LIMIT :cap
        )
        """
    )
    suspend fun deleteBeyondCap(cap: Int)
}
