package com.example.serviceapp.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object ImageUploader {

    private val storage get() = FirebaseStorage.getInstance()

    /**
     * If [uriString] is a local content:// URI, uploads it to
     * Firebase Storage at providers/{uid}/{filename} and returns the
     * public download URL.  Otherwise returns [uriString] unchanged
     * (handles preset-avatar strings and existing https:// URLs).
     */
    suspend fun uploadIfLocal(uid: String, uriString: String, filename: String): String {
        if (uriString.isBlank() || !uriString.startsWith("content://")) return uriString
        return try {
            val ref = storage.reference.child("providers/$uid/$filename")
            ref.putFile(Uri.parse(uriString)).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            uriString  // on upload failure fall back to the local URI
        }
    }
}
