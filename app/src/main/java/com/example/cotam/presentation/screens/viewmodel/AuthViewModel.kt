package com.example.cotam.presentation.screens.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.cotam.data.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    val isAuthLoading = authRepo.isAuthLoading
    val currentUserId: StateFlow<String?> = authRepo.currentUserId
    val isSignedIn = authRepo.isSignedIn

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

    fun signOut(){
        authRepo.signOut()
    }

    fun forgotPassword(email:String, context: Context){
        authRepo.forgotPassword(email, context)
    }
}