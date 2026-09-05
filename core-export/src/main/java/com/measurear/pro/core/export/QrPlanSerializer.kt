package com.measurear.pro.core.export

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/**
 * PRD Section 6 differentiator: serialize a RoomPlan to compact JSON, encode
 * as a QR bitmap (zxing-android-embedded). Payload must stay within ~2-3KB —
 * QR capacity depends on error-correction level; large multi-room plans should
 * split across multiple codes or fall back to file export rather than silently
 * failing here. Pro-only feature (gated by the calling feature module).
 */
class QrPlanSerializer {

    fun encodeToQrBitmap(jsonPayload: String, sizePx: Int = 512): Bitmap {
        val bitMatrix = QRCodeWriter().encode(jsonPayload, BarcodeFormat.QR_CODE, sizePx, sizePx)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.RGB_565)
        for (x in 0 until sizePx) {
            for (y in 0 until sizePx) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }

    // Decoding a scanned QR back into a RoomPlan is a camera-scan flow (separate
    // screen with its own CAMERA permission check) — left for the Phase 5 feature
    // module to implement using zxing's IntentIntegrator or CaptureActivity,
    // rather than guessed at here without that screen's context.
}
