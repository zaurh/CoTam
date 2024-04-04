package com.zaurh.cotam.presentation.screens.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.zaurh.cotam.data.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    val isAuthLoading = authRepo.isAuthLoading
    val currentUserId = authRepo.currentUserId
    val isSignedIn = authRepo.isSignedIn

    init {
        isSignedIn.value = authRepo.currentUser != null
    }

    fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        context: Context,
        username: String,
        token: String
    ) {
        authRepo.signUp(email, password, confirmPassword, context, username, token)
    }

    fun signIn(email: String, password: String, context: Context){
        authRepo.signIn(email, password, context)
    }


    fun forgotPassword(email:String, context: Context){
        authRepo.forgotPassword(email, context)
    }
}