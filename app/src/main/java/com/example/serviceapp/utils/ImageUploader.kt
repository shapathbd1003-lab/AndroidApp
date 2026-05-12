package com.example.serviceapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object ImageUploader {

    // ── Replace with your imgBB API key from https://api.imgbb.com ──────────
    private const val IMGBB_API_KEY = "YOUR_IMGBB_API_KEY_HERE"
    // ────────────────────────────────────────────────────────────────────────

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * If [uriString] is a local content:// URI, compresses and uploads
     * it to imgBB (free, no billing required) and returns the public HTTPS
     * URL.  Otherwise returns [uriString] unchanged (preset avatars, existing
     * https:// URLs, empty strings).
     */
    suspend fun uploadIfLocal(uid: String, uriString: String, filename: String): String {
        if (uriString.isBlank() || !uriString.startsWith("content://")) return uriString
        val ctx = appContext ?: return uriString
        return try {
            val base64 = compressAndEncode(ctx, Uri.parse(uriString))
            uploadToImgBB(base64) ?: uriString
        } catch (e: Exception) {
            uriString
        }
    }

    /** Decode, scale down to max 1024px, and encode as Base64. */
    private fun compressAndEncode(ctx: Context, uri: Uri): String {
        val stream = ctx.contentResolver.openInputStream(uri)!!
        val original = BitmapFactory.decodeStream(stream)
        stream.close()

        val maxDim = 1024
        val scaled = if (original.width > maxDim || original.height > maxDim) {
            val scale = maxDim.toFloat() / maxOf(original.width, original.height)
            Bitmap.createScaledBitmap(
                original,
                (original.width * scale).toInt(),
                (original.height * scale).toInt(),
                true
            )
        } else original

        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, out)
        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)
    }

    /** POST base64 image to imgBB and return the public URL. */
    private suspend fun uploadToImgBB(base64: String): String? = withContext(Dispatchers.IO) {
        val url = URL("https://api.imgbb.com/1/upload?key=$IMGBB_API_KEY")
        val body = "image=${URLEncoder.encode(base64, "UTF-8")}"

        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        conn.outputStream.use { it.write(body.toByteArray()) }

        val response = conn.inputStream.bufferedReader().readText()
        conn.disconnect()

        // Extract "url" from JSON without an extra library
        Regex("\"url\":\"([^\"]+)\"").find(response)
            ?.groupValues?.get(1)
            ?.replace("\\/", "/")
    }
}
