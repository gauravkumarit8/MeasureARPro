package com.measurear.pro.domain.usecase

/**
 * Pro Room Planner cost estimate — pure formula layer, no new sensors needed.
 * coveragePerUnit: e.g. sq meters covered per paint can/liter, or per tile box.
 * pricePerUnit: user-entered local price.
 */
fun estimateMaterialCost(areaSquareMeters: Float, coveragePerUnit: Float, pricePerUnit: Float): CostEstimate {
    val unitsNeeded = kotlin.math.ceil(areaSquareMeters / coveragePerUnit).toInt().coerceAtLeast(1)
    return CostEstimate(unitsNeeded = unitsNeeded, totalCost = unitsNeeded * pricePerUnit)
}

data class CostEstimate(val unitsNeeded: Int, val totalCost: Float)

/** Curtain width template — default gathering allowance factor per Section 6 of the PRD. */
fun curtainFabricWidth(measuredWidthMeters: Float, allowanceFactor: Float = 2.25f): Float =
    measuredWidthMeters * allowanceFactor
