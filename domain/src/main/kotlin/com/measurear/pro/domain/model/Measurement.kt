package com.measurear.pro.domain.model

/** A single AR distance measurement, with the 3-sample confidence score attached. */
data class Measurement(
    val id: String,
    val distanceMeters: Double,
    val confidenceSpreadCm: Double,
    val confidence: ConfidenceLevel,
    val timestampEpochMillis: Long,
    val photoUri: String? = null
)

enum class ConfidenceLevel { HIGH, MEDIUM, LOW }

/** Maps a 3-sample spread (in cm) to a ConfidenceLevel per the PRD's Section 6 rule. */
fun spreadToConfidence(spreadCm: Double): ConfidenceLevel = when {
    spreadCm < 1.0 -> ConfidenceLevel.HIGH
    spreadCm <= 3.0 -> ConfidenceLevel.MEDIUM
    else -> ConfidenceLevel.LOW
}
