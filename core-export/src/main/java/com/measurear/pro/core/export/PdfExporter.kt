package com.measurear.pro.core.export

import android.content.Context
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Renders a RoomPlan or Measurement to PDF using Android's on-device
 * PdfDocument API — no server round-trip, consistent with offline-first.
 *
 * IMPORTANT (Play Store compliance): files are written to the app's private
 * cache dir and shared out via FileProvider content:// URIs (see
 * res/xml/file_paths.xml and the <provider> entry in :app's manifest) — never
 * a raw file:// Uri, which throws FileUriExposedException on API 24+ and
 * would crash on share.
 *
 * Free tier: includeWatermark = true (enforced by the calling feature module
 * based on EntitlementState, not here — this class just draws what it's told to).
 */
class PdfExporter(private val context: Context) {

    fun exportSimpleTextDocument(fileName: String, title: String, lines: List<String>, includeWatermark: Boolean): android.net.Uri {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 at 72dpi
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint().apply { textSize = 18f }

        canvas.drawText(title, 40f, 60f, paint.apply { textSize = 22f; isFakeBoldText = true })
        paint.apply { textSize = 14f; isFakeBoldText = false }
        lines.forEachIndexed { index, line ->
            canvas.drawText(line, 40f, 100f + index * 24f, paint)
        }
        if (includeWatermark) {
            val watermarkPaint = android.graphics.Paint().apply {
                textSize = 48f
                alpha = 60
                color = android.graphics.Color.GRAY
            }
            canvas.save()
            canvas.rotate(-30f, 300f, 400f)
            canvas.drawText("MeasureAR Pro \u2014 Free", 120f, 420f, watermarkPaint)
            canvas.restore()
        }
        document.finishPage(page)

        val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportsDir, "$fileName.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        return FileProvider.getUriForFile(context, "com.measurear.pro.fileprovider", file)
    }
}
