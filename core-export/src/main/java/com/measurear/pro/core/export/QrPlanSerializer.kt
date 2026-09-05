package com.measurear.pro.core.export

/**
 * PRD Section 6 differentiator: serialize a RoomPlan to compact JSON, compress,
 * encode as QR (zxing-android-embedded). Payload must stay within ~2-3KB after
 * compression — large multi-room plans split across multiple codes or fall back
 * to file export. Pro-only feature.
 */
interface QrPlanSerializer {
    fun encodeToQrBitmap(roomPlanId: String): android.graphics.Bitmap
    fun decodeFromQrPayload(payload: String): Result<String> // returns roomPlanId on success
}
