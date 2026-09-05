package com.measurear.pro.core.database.repository

import com.measurear.pro.core.database.dao.MeasurementDao
import com.measurear.pro.core.database.entities.MeasurementEntity
import com.measurear.pro.domain.model.ConfidenceLevel
import com.measurear.pro.domain.model.Measurement
import com.measurear.pro.domain.repository.MeasurementRepository

/**
 * Free tier caps stored history at 5 entries (PRD Section 3.1) — enforced here
 * via deleteBeyondCap after every insert, not in the DAO's schema. Pro tier
 * (unlimited history, Section 3.2) will call clearOldestIfOverCap with a very
 * high cap once that entitlement check exists, rather than skipping the call
 * entirely — keeps one code path instead of two.
 */
class RoomMeasurementRepository(private val dao: MeasurementDao) : MeasurementRepository {

    companion object {
        const val FREE_TIER_CAP = 5
    }

    override suspend fun save(measurement: Measurement) {
        dao.insert(measurement.toEntity())
        clearOldestIfOverCap(FREE_TIER_CAP)
    }

    override suspend fun recent(limit: Int): List<Measurement> =
        dao.recent(limit).map { it.toDomain() }

    override suspend fun all(): List<Measurement> =
        dao.all().map { it.toDomain() }

    override suspend fun clearOldestIfOverCap(cap: Int) {
        dao.deleteBeyondCap(cap)
    }
}

private fun Measurement.toEntity() = MeasurementEntity(
    id = id,
    distanceMeters = distanceMeters,
    confidenceSpreadCm = confidenceSpreadCm,
    confidenceLevel = confidence.name,
    timestampEpochMillis = timestampEpochMillis,
    photoUri = photoUri
)

private fun MeasurementEntity.toDomain() = Measurement(
    id = id,
    distanceMeters = distanceMeters,
    confidenceSpreadCm = confidenceSpreadCm,
    confidence = ConfidenceLevel.valueOf(confidenceLevel),
    timestampEpochMillis = timestampEpochMillis,
    photoUri = photoUri
)
