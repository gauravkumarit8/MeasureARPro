package com.measurear.pro.core.export

/**
 * Renders a RoomPlan or Measurement to PDF using Android's on-device
 * PdfDocument API — no server round-trip, consistent with the offline-first
 * architecture. Pro tier only (free tier exports carry a watermark instead —
 * enforce that in the feature module's export button, not here).
 */
interface PdfExporter {
    fun exportRoomPlan(roomPlanId: String, includeWatermark: Boolean): android.net.Uri
    fun exportMeasurement(measurementId: String, includeWatermark: Boolean): android.net.Uri
}
