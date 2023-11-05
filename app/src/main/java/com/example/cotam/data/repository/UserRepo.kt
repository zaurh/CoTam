package com.example.cotam.data.repository

import androidx.compose.runtime.mutableStateOf
import com.example.cotam.data.remote.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    auth: FirebaseAuth,
) {


    val isUserLoading = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val usersData = mutableStateOf<List<UserData>>(emptyList())
    val currentUserId = auth.currentUser?.uid



    init {
        getAllUsers()
        auth.currentUser?.uid?.let {
            getUserData(it)
        }

    }

    fun addUser(userData: UserData) {
        if (currentUserId != null) {
            firestore.collection("user").document(userData.userId ?: "").set(userData)
                .addOnSuccessListener {
                    isUserLoading.value = false
                }
                .addOnFailureListener {
                    isUserLoading.value = false
                }
        }

    }

    fun getUserData(userId: String) {
        if (currentUserId != null) {
            firestore.collection("user").document(userId).get()
                .addOnSuccessListener {
                    this.userData.value = it.toObject<UserData>()
                }
        }
    }

    private fun getAllUsers() {
        firestore.collection("user")
            .addSnapshotListener { value, _ ->
                value?.let { it ->
                    usersData.value =
                        it.toObjects<UserData>().sortedBy { it.username }
                }
            }
    }

    fun updateUser(
        userData: UserData
    ) {
        if (currentUserId != null) {
            firestore.collection("user").document(currentUserId).update(userData.toMap())
                .addOnSuccessListener {
                    this.userData.value = userData

                }

            firestore.collection("message")
                .whereEqualTo("senderUserId", currentUserId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.update("senderUsername", userData.username)
                    }
                }

        }
    }

}