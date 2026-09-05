package com.measurear.pro.core.ar

import com.measurear.pro.domain.model.ConfidenceLevel
import com.measurear.pro.domain.model.spreadToConfidence

/**
 * PRD Section 6 differentiator: take 3 quick AR samples of the same two points
 * and report a confidence label instead of a single point reading.
 */
class ConfidenceScorer {
    fun score(samplesMeters: List<Double>): Pair<Double, ConfidenceLevel> {
        require(samplesMeters.isNotEmpty()) { "Need at least one sample" }
        val spreadCm = (samplesMeters.max() - samplesMeters.min()) * 100.0
        return spreadCm to spreadToConfidence(spreadCm)
    }
}
