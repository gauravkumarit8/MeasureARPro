package com.measurear.pro.domain.model

data class WallSegment(val startX: Float, val startY: Float, val endX: Float, val endY: Float) {
    val lengthMeters: Float
        get() = kotlin.math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY))
}

data class RoomPlan(
    val id: String,
    val name: String,
    val walls: List<WallSegment>,
    val ceilingHeightMeters: Float,
    val doors: Int = 0,
    val windows: Int = 0
) {
    val perimeterMeters: Float get() = walls.sumOf { it.lengthMeters.toDouble() }.toFloat()
    // Shoelace formula — valid for a closed, simple (non-self-intersecting) polygon of wall points.
    val areaSquareMeters: Float
        get() {
            if (walls.size < 3) return 0f
            var sum = 0f
            for (w in walls) sum += (w.startX * w.endY - w.endX * w.startY)
            return kotlin.math.abs(sum / 2f)
        }
}
