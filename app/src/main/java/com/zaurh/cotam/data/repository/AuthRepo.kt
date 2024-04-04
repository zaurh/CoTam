package com.zaurh.cotam.data.repository

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.zaurh.cotam.data.remote.UserData
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepo: UserRepo
) {

    var currentUser = auth.currentUser
    var currentUserId = auth.currentUser?.uid

    val isAuthLoading = mutableStateOf(false)
    val isSignedIn = mutableStateOf(false)

    init {
        isSignedIn.value = auth.currentUser != null
    }


    fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        context: Context,
        username: String,
        token: String
    ) {
        isAuthLoading.value = true
        if (!checkValidEmail(email)) {
            Toast.makeText(context, "Email is not valid.", Toast.LENGTH_SHORT).show()
            isAuthLoading.value = false
        } else if (password.length < 8) {
            Toast.makeText(context, "Password should be at least 8 characters.", Toast.LENGTH_SHORT)
                .show()
            isAuthLoading.value = false
        } else if (password != confirmPassword) {
            Toast.makeText(context, "Passwords don't match!", Toast.LENGTH_SHORT).show()
            isAuthLoading.value = false
        } else {
            checkEmailExistence(email = email, context)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    isAuthLoading.value = false
                    isSignedIn.value = true
                    userRepo.addUser(
                        UserData(
                            userId = auth.currentUser?.uid.toString(),
                            username = username,
                            token = token
                        )
                    )
                }
                .addOnFailureListener {
                    isAuthLoading.value = false
                }
        }

    }

    fun signIn(email: String, password: String, context: Context) {

        isAuthLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                auth.currentUser?.let {
                    isAuthLoading.value = false
                    isSignedIn.value = true
                }
            }
            .addOnFailureListener {
                isAuthLoading.value = false
                Toast.makeText(context, "Email or password is incorrect", Toast.LENGTH_SHORT).show()
            }
    }

    fun signOut() {
        auth.signOut()
        isSignedIn.value = false
    }

    fun forgotPassword(email: String, context: Context) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            Toast.makeText(context, "Sent. Please check your email.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(
                context,
                "Problem occurred. Please enter valid email.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //*****************   Firebase AUTH Catching errors   *********************

    private fun checkEmailExistence(email: String, context: Context) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result?.signInMethods?.isEmpty() == true) {
                        // Email does not exist
                    } else {
                        Toast.makeText(context, "Email is already registered.", Toast.LENGTH_SHORT)
                            .show()
                        isAuthLoading.value = false
                    }
                } else {
                    // Error occurred
                }
            }
    }

    private fun checkValidEmail(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }
}