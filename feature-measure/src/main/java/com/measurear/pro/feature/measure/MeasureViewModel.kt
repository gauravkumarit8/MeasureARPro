package com.measurear.pro.feature.measure

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MeasureUiState(val readoutText: String = "Tap two points to measure")

/**
 * Phase 0 stub. Phase 1: inject ArSessionManager + ConfidenceScorer (:core-ar)
 * and MeasurementRepository (:core-database via :domain interface) to persist
 * results and enforce the free-tier 5-item history cap (PRD Section 3.1).
 */
class MeasureViewModel {
    private val _uiState = MutableStateFlow(MeasureUiState())
    val uiState: StateFlow<MeasureUiState> = _uiState

    fun onTapSetPoint() {
        // Phase 1: call ArSessionManager.placePoint(), accumulate 3 samples,
        // run ConfidenceScorer, update _uiState with "X m \u00b1Ycm, confidence".
    }
}
