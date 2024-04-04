package com.zaurh.cotam.data.repository

import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.zaurh.cotam.data.remote.UserData
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
) {


    private val isUserLoading = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val usersData = mutableStateOf<List<UserData>>(emptyList())
    val currentUserId = auth.currentUser?.uid


    init {
        getAllUsers()
    }

    fun addUser(userData: UserData) {
        firestore.collection("user").document(userData.userId ?: "").set(userData)
            .addOnSuccessListener {
                isUserLoading.value = false
            }
            .addOnFailureListener {
                isUserLoading.value = false
            }

    }

    fun getUserData(userId: String) {
        firestore.collection("user").document(userId).get()
            .addOnSuccessListener {
                this.userData.value = it.toObject<UserData>()
            }
    }

    fun getAllUsers() {
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
        firestore.collection("user").document(userData.userId ?: "").update(userData.toMap())
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