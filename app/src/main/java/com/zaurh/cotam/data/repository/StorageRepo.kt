package com.zaurh.cotam.data.repository

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import javax.inject.Inject

class StorageRepo @Inject constructor(
    private val storage: FirebaseStorage
) {

    val isMediaLoading = mutableStateOf(false)


    fun uploadMedia(uri: Uri, path: String, onSuccess: (Uri) -> Unit) {
        isMediaLoading.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val fileRef = storageRef.child("$path/$uuid")
        val uploadTask = fileRef.putFile(uri)

        uploadTask
            .addOnSuccessListener {
                isMediaLoading.value = false
                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
            }
    }
}