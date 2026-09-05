package com.measurear.pro.domain.repository

import com.measurear.pro.domain.model.Measurement

/** Implemented by :core-database. Domain layer depends only on this interface. */
interface MeasurementRepository {
    suspend fun save(measurement: Measurement)
    suspend fun recent(limit: Int): List<Measurement>
    suspend fun all(): List<Measurement>
    suspend fun clearOldestIfOverCap(cap: Int)
}
