package com.example.serviceapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUploader {

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Converts a local content:// URI to a compressed base64 data URI
     * (e.g. "data:image/jpeg;base64,/9j/...") that can be stored directly
     * in Firestore and rendered by any browser/Compose image loader.
     *
     * Returns [uriString] unchanged when it is already an https:// URL,
     * a data URI, a preset-avatar string, or empty.
     */
    suspend fun uploadIfLocal(uid: String, uriString: String, filename: String): String {
        if (uriString.isBlank()
            || !uriString.startsWith("content://")) return uriString
        val ctx = appContext ?: return uriString
        return try {
            toDataUri(ctx, Uri.parse(uriString), filename)
        } catch (e: Exception) {
            uriString
        }
    }

    private fun toDataUri(ctx: Context, uri: Uri, filename: String): String {
        val stream = ctx.contentResolver.openInputStream(uri)!!
        val original = BitmapFactory.decodeStream(stream)
        stream.close()

        // Certificates → max 900px; profile photos → max 400px
        val maxDim = if (filename.contains("certificate")) 900 else 400
        val quality = if (filename.contains("certificate")) 75  else 70

        val scaled = if (original.width > maxDim || original.height > maxDim) {
            val scale = maxDim.toFloat() / maxOf(original.width, original.height)
            Bitmap.createScaledBitmap(
                original,
                (original.width  * scale).toInt(),
                (original.height * scale).toInt(),
                true
            )
        } else original

        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)
        val b64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
        return "data:image/jpeg;base64,$b64"
    }
}
