package com.measurear.pro.feature.measure

import com.measurear.pro.core.ar.ArPoint
import com.measurear.pro.core.ar.ArSessionManager
import com.measurear.pro.core.ar.ConfidenceScorer
import com.measurear.pro.core.ar.PlacedPoint
import com.measurear.pro.core.ar.distanceBetween
import com.measurear.pro.domain.model.ConfidenceLevel
import com.measurear.pro.domain.model.Measurement
import com.measurear.pro.domain.repository.MeasurementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class MeasureStep { PLACE_FIRST_POINT, PLACE_SECOND_POINT, SAMPLING, DONE }

data class MeasureUiState(
    val step: MeasureStep = MeasureStep.PLACE_FIRST_POINT,
    val readoutText: String = "Tap the first point",
    val distanceMeters: Double? = null,
    val confidenceSpreadCm: Double? = null,
    val confidenceLevel: ConfidenceLevel? = null,
    // Exposed so ArSceneContent can render a marker node at each placed point.
    // Only ever holds 0-2 anchors (the two measurement points).
    val activeAnchors: List<com.google.ar.core.Anchor> = emptyList()
)

/**
 * Drives the free-tier AR Distance Measure flow (PRD Wireframe 1 + Section 6
 * Accuracy Confidence Score differentiator): place two points, then take 3
 * quick re-samples of the same pair to report a confidence-scored distance
 * instead of a single reading.
 *
 * Persistence to the free-tier 5-item history cap (:core-database) is wired
 * via MeasurementRepository — every completed measurement is saved and the
 * repository itself enforces the cap (RoomMeasurementRepository.FREE_TIER_CAP).
 *
 * NOTE: intentionally a plain class, not androidx.lifecycle.ViewModel, to avoid
 * pulling in lifecycle-viewmodel-compose before it's needed elsewhere. It's held
 * via `remember { }` in MeasureScreen for now, so it does NOT survive process
 * death / config changes yet — promote to a real ViewModel in a later phase once
 * more screens need that (avoids adding the dependency for just one screen).
 * The manual CoroutineScope below has the same limitation: it's cancelled
 * explicitly via onCleared(), which MeasureScreen must call from a
 * DisposableEffect, since there's no ViewModel lifecycle doing it automatically.
 */
class MeasureViewModel(
    private val arSessionManager: ArSessionManager,
    private val measurementRepository: MeasurementRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(MeasureUiState())
    val uiState: StateFlow<MeasureUiState> = _uiState

    private var firstPoint: ArPoint? = null
    private var secondPoint: ArPoint? = null
    private val samples = mutableListOf<Double>()
    private val scorer = ConfidenceScorer()
    private val samplesNeeded = 3

    fun onTapAt(screenX: Float, screenY: Float) {
        val placed: PlacedPoint = arSessionManager.placePoint(screenX, screenY) ?: run {
            _uiState.value = _uiState.value.copy(readoutText = "Move slowly to detect a surface, then tap")
            return
        }
        val point = placed.position

        when (_uiState.value.step) {
            MeasureStep.PLACE_FIRST_POINT -> {
                firstPoint = point
                _uiState.value = _uiState.value.copy(
                    step = MeasureStep.PLACE_SECOND_POINT,
                    readoutText = "Tap the second point",
                    activeAnchors = listOf(placed.anchor)
                )
            }
            MeasureStep.PLACE_SECOND_POINT -> {
                secondPoint = point
                samples.clear()
                samples.add(distanceBetween(firstPoint!!, point))
                _uiState.value = _uiState.value.copy(
                    step = MeasureStep.SAMPLING,
                    readoutText = "Hold steady \u2014 sampling ${samples.size}/$samplesNeeded",
                    activeAnchors = _uiState.value.activeAnchors + placed.anchor
                )
            }
            MeasureStep.SAMPLING -> {
                // Re-hit-test the same two screen positions isn't required — for a
                // steady hand, re-tapping near the same physical points is enough
                // to capture natural AR tracking jitter for the confidence score.
                // The re-sample's own anchor is discarded immediately (detach) —
                // only the original two anchors stay visible as markers.
                placed.anchor.detach()
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
        _uiState.value = _uiState.value.copy(
            step = MeasureStep.DONE,
            readoutText = formatReadout(avgDistance, spreadCm, confidence),
            distanceMeters = avgDistance,
            confidenceSpreadCm = spreadCm,
            confidenceLevel = confidence
            // activeAnchors intentionally left as-is — markers stay visible
            // showing the two measured points until the user resets.
        )
        persistMeasurement(avgDistance, spreadCm, confidence)
    }

    private fun persistMeasurement(distanceMeters: Double, spreadCm: Double, confidence: ConfidenceLevel) {
        val measurement = Measurement(
            id = UUID.randomUUID().toString(),
            distanceMeters = distanceMeters,
            confidenceSpreadCm = spreadCm,
            confidence = confidence,
            timestampEpochMillis = System.currentTimeMillis()
        )
        scope.launch {
            measurementRepository.save(measurement)
        }
    }

    /** Call from MeasureScreen's DisposableEffect(onDispose) — see class doc comment. */
    fun onCleared() {
        _uiState.value.activeAnchors.forEach { it.detach() }
        scope.cancel()
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
        // Detach old anchors before dropping references — undetached anchors
        // keep consuming ARCore tracking resources indefinitely.
        _uiState.value.activeAnchors.forEach { it.detach() }
        firstPoint = null
        secondPoint = null
        samples.clear()
        _uiState.value = MeasureUiState()
    }
}
