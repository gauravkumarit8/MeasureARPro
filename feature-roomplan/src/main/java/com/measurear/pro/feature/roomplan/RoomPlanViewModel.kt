package com.measurear.pro.feature.roomplan

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class RoomPlanUiState(
    val perimeterMeters: Float = 0f,
    val areaSquareMeters: Float = 0f,
    val ceilingHeightMeters: Float = 0f,
    val costEstimateText: String? = null
)

/**
 * Phase 3: wire ArSessionManager for wall capture, domain.model.RoomPlan for the
 * perimeter/area math (shoelace formula, already implemented in :domain), and
 * domain.usecase.estimateMaterialCost for the cost line shown in Wireframe 3.
 */
class RoomPlanViewModel {
    private val _uiState = MutableStateFlow(RoomPlanUiState())
    val uiState: StateFlow<RoomPlanUiState> = _uiState
}
