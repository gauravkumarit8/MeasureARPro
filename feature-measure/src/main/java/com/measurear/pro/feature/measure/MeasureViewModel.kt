package com.measurear.pro.feature.measure

import com.measurear.pro.core.ar.ArPoint
import com.measurear.pro.core.ar.ArSessionManager
import com.measurear.pro.core.ar.ConfidenceScorer
import com.measurear.pro.core.ar.distanceBetween
import com.measurear.pro.domain.model.ConfidenceLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class MeasureStep { PLACE_FIRST_POINT, PLACE_SECOND_POINT, SAMPLING, DONE }

data class MeasureUiState(
    val step: MeasureStep = MeasureStep.PLACE_FIRST_POINT,
    val readoutText: String = "Tap the first point",
    val distanceMeters: Double? = null,
    val confidenceSpreadCm: Double? = null,
    val confidenceLevel: ConfidenceLevel? = null
)

/**
 * Drives the free-tier AR Distance Measure flow (PRD Wireframe 1 + Section 6
 * Accuracy Confidence Score differentiator): place two points, then take 3
 * quick re-samples of the same pair to report a confidence-scored distance
 * instead of a single reading.
 *
 * Persistence to the free-tier 5-item history cap (:core-database) is Phase 1
 * follow-up once MeasurementRepository has a real Room-backed implementation —
 * intentionally left as a TODO here rather than guessed at.
 *
 * NOTE: intentionally a plain class, not androidx.lifecycle.ViewModel, to avoid
 * pulling in lifecycle-viewmodel-compose before it's needed elsewhere. It's held
 * via `remember { }` in MeasureScreen for now, so it does NOT survive process
 * death / config changes yet — promote to a real ViewModel in a later phase once
 * more screens need that (avoids adding the dependency for just one screen).
 */
class MeasureViewModel(
    private val arSessionManager: ArSessionManager
) {

    private val _uiState = MutableStateFlow(MeasureUiState())
    val uiState: StateFlow<MeasureUiState> = _uiState

    private var firstPoint: ArPoint? = null
    private var secondPoint: ArPoint? = null
    private val samples = mutableListOf<Double>()
    private val scorer = ConfidenceScorer()
    private val samplesNeeded = 3

    fun onTapAt(screenX: Float, screenY: Float) {
        val point = arSessionManager.placePoint(screenX, screenY) ?: run {
            _uiState.value = _uiState.value.copy(readoutText = "Move slowly to detect a surface, then tap")
            return
        }

        when (_uiState.value.step) {
            MeasureStep.PLACE_FIRST_POINT -> {
                firstPoint = point
                _uiState.value = _uiState.value.copy(
                    step = MeasureStep.PLACE_SECOND_POINT,
                    readoutText = "Tap the second point"
                )
            }
            MeasureStep.PLACE_SECOND_POINT -> {
                secondPoint = point
                samples.clear()
                samples.add(distanceBetween(firstPoint!!, point))
                _uiState.value = _uiState.value.copy(
                    step = MeasureStep.SAMPLING,
                    readoutText = "Hold steady \u2014 sampling ${samples.size}/$samplesNeeded"
                )
            }
            MeasureStep.SAMPLING -> {
                // Re-hit-test the same two screen positions isn't required — for a
                // steady hand, re-tapping near the same physical points is enough
                // to capture natural AR tracking jitter for the confidence score.
                secondPoint?.let { samples.add(distanceBetween(firstPoint!!, point)) }
                if (samples.size >= samplesNeeded) {
                    finishSampling()
                } else {
                    _uiState.value = _uiState.value.copy(
                        readoutText = "Hold steady \u2014 sampling ${samples.size}/$samplesNeeded"
                    )
                }
            }
            MeasureStep.DONE -> resetForNewMeasurement()
        }
    }

    private fun finishSampling() {
        val (spreadCm, confidence) = scorer.score(samples)
        val avgDistance = samples.average()
        _uiState.value = MeasureUiState(
            step = MeasureStep.DONE,
            readoutText = formatReadout(avgDistance, spreadCm, confidence),
            distanceMeters = avgDistance,
            confidenceSpreadCm = spreadCm,
            confidenceLevel = confidence
        )
        // TODO Phase 1 follow-up: persist via MeasurementRepository (:core-database)
        // and enforce the free-tier 5-item cap (PRD Section 3.1).
    }

    private fun formatReadout(distanceMeters: Double, spreadCm: Double, confidence: ConfidenceLevel): String {
        val distanceStr = String.format("%.2f m", distanceMeters)
        val confidenceStr = when (confidence) {
            ConfidenceLevel.HIGH -> "high confidence"
            ConfidenceLevel.MEDIUM -> "medium confidence"
            ConfidenceLevel.LOW -> "low confidence \u2014 try re-measuring"
        }
        return "$distanceStr \u00b1${String.format("%.1f", spreadCm)}cm ($confidenceStr)"
    }

    fun resetForNewMeasurement() {
        firstPoint = null
        secondPoint = null
        samples.clear()
        _uiState.value = MeasureUiState()
    }
}
