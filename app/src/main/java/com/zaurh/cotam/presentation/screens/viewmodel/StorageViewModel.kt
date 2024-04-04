package com.zaurh.cotam.presentation.screens.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.zaurh.cotam.data.repository.StorageRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageRepo: StorageRepo
): ViewModel() {

    val isMediaLoading = storageRepo.isMediaLoading

    fun uploadMedia(uri: Uri, path: String, onSuccess: (Uri) -> Unit) {
        storageRepo.uploadMedia(uri, path, onSuccess)
    }
}