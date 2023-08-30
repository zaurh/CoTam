package com.example.cotam.presentation

import android.content.Context
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cotam.data.MessageData
import com.example.cotam.data.NotificationData
import com.example.cotam.data.PushNotification
import com.example.cotam.data.UserData
import com.example.cotam.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val repository: NotificationRepository
) : ViewModel() {

    val messagesData = mutableStateOf<List<MessageData>>(emptyList())
    val messageData = mutableStateOf<MessageData?>(null)

    val allMessagesAsync = mutableStateOf<List<MessageData>>(emptyList())
    val allMessagesSync = mutableStateOf<List<MessageData>>(emptyList())
    val userData = mutableStateOf<UserData?>(null)
    val usersData = mutableStateOf<List<UserData>>(emptyList())
    val isLoading = mutableStateOf(false)
    val isImageLoading = mutableStateOf(false)
    val isSignedIn = mutableStateOf(false)

    var replyingPerson = mutableStateOf("")
    var replyingMessage = mutableStateOf("")
    var replyingImage = mutableStateOf("")
    var replyingVideo = mutableStateOf("")
    var isReplyingState = mutableStateOf(false)

    val selectedMessages = mutableStateListOf<MessageData>()


    private var isSearchStarting = true
    private var initialUsers = listOf<UserData>()


    init {
        getMessagesSync()
        getAllUsers()
        auth.currentUser?.uid?.let {
            getUserData(it)
        }
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
        isLoading.value = true
        if (!checkValidEmail(email)) {
            Toast.makeText(context, "Email is not valid.", Toast.LENGTH_SHORT).show()
            isLoading.value = false
        } else if (password.length < 8) {
            Toast.makeText(context, "Password should be at least 8 characters.", Toast.LENGTH_SHORT)
                .show()
            isLoading.value = false
        } else if (password != confirmPassword) {
            Toast.makeText(context, "Passwords don't match!", Toast.LENGTH_SHORT).show()
            isLoading.value = false
        } else {
            checkEmailExistence(email = email, context)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    isLoading.value = false
                    isSignedIn.value = true
                    addUser(username, token)
                }
                .addOnFailureListener {
                    isLoading.value = false
                }
        }

    }

    fun signIn(email: String, password: String, context: Context) {

        isLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                auth.currentUser?.let {
                    isLoading.value = false
                    isSignedIn.value = true
                }
            }
            .addOnFailureListener {
                isLoading.value = false
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
                        isLoading.value = false
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


    //***********************    Firebase Firestore   **********************

    private fun addUser(username: String, token: String) {
        val userId = auth.currentUser?.uid
        val userdata = UserData(
            username = username,
            userId = userId,
            token = token
        )
        userId?.let { uid ->
            firestore.collection("user").document(uid).set(userdata)
                .addOnSuccessListener {
                    isLoading.value = false
                }
                .addOnFailureListener {
                    isLoading.value = false
                }
        }
    }

    private fun getUserData(userId: String) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            firestore.collection("user").document(userId).get()
                .addOnSuccessListener {
                    userData.value = it.toObject<UserData>()
                }
        }
    }

    private fun getMessageData(messageId: String) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            firestore.collection("message").document(messageId).get()
                .addOnSuccessListener {
                    messageData.value = it.toObject<MessageData>()
                }
        }
    }

    private fun getAllUsers() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            firestore.collection("user")
                .addSnapshotListener { value, _ ->
                    value?.let { it ->
                        usersData.value =
                            it.toObjects<UserData>().sortedBy { it.username }
                    }
                }
        }
    }

    fun updateUser(
        username: String? = null,
        imageUrl: String? = null,
        token: String? = null
    ) {
        val currentUserId = auth.currentUser?.uid
        val updateUserData = UserData(
            username = username ?: userData.value?.username,
            image = imageUrl ?: userData.value?.image,
            gotMsgFrom = userData.value?.gotMsgFrom ?: listOf(),
            sendMsgTo = userData.value?.sendMsgTo ?: listOf(),
            token = token ?: userData.value?.token
        )
        if (currentUserId != null) {
            firestore.collection("user").document(currentUserId).update(updateUserData.toMap())
                .addOnSuccessListener {
                    this.userData.value = updateUserData
                }

            firestore.collection("message")
                .whereEqualTo("senderUserId", currentUserId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.update("senderUsername", username)
                    }
                }
        }
    }

    //Uploading pictures

    fun uploadProfileImage(uri: Uri) {
        val user = userData.value
        user?.let {
            uploadingImage(uri) {
                updateUser(
                    username = user.username,
                    imageUrl = it.toString(),
                )
            }
        }
    }

    fun sendImage(
        uri: Uri,
        message: String,
        getterUserId: String,
        getterUsername: String,
        getterUserImage: String,
        getterToken: String
    ) {
        val user = userData.value
        user?.let {
            uploadingImage(uri) {
                sendMessage(
                    imageUrl = it.toString(),
                    message = message,
                    getterUserId = getterUserId,
                    getterUserImage = getterUserImage,
                    getterUsername = getterUsername,
                    getterToken = getterToken
                )
            }
        }
    }

    fun sendVideo(
        uri: Uri,
        message: String,
        getterUserId: String,
        getterUsername: String,
        getterUserImage: String,
        getterToken: String
    ) {
        val user = userData.value
        user?.let {
            uploadingVideo(uri) {
                sendMessage(
                    videoUrl = it.toString(),
                    message = message,
                    getterUserId = getterUserId,
                    getterUserImage = getterUserImage,
                    getterUsername = getterUsername,
                    getterToken = getterToken
                )
            }
        }
    }

    private fun uploadingImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        isImageLoading.value = true

        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask
            .addOnSuccessListener {
                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
                isImageLoading.value = false
            }
            .addOnFailureListener {
                isImageLoading.value = false
            }
    }

    private fun uploadingVideo(uri: Uri, onSuccess: (Uri) -> Unit) {
        isImageLoading.value = true

        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val videoRef = storageRef.child("video/$uuid")
        val uploadTask = videoRef.putFile(uri)

        uploadTask
            .addOnSuccessListener {
                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
                isImageLoading.value = false
            }
            .addOnFailureListener {
                isImageLoading.value = false
            }
    }


    ////MESSENGER

    fun sendMessage(
        imageUrl: String = "",
        videoUrl: String = "",
        replyMessage: String? = null,
        replyImage: String? = null,
        replyVideo: String? = null,
        message: String,
        getterUsername: String,
        getterUserImage: String,
        getterUserId: String,
        getterToken: String
    ) {

        sendPrivateMessage(
            imageUrl = imageUrl,
            videoUrl = videoUrl,
            message = message,
            getterUsername,
            getterUserImage,
            getterUserId,
            getterToken,
            replyMessage = replyMessage,
            replyImage = replyImage,
            replyVideo = replyVideo
        )
        val notificationData = NotificationData(
            title = userData.value?.username ?: "",
            text = message
        )

        val pushNotification = PushNotification(data = notificationData, to = getterToken)
        viewModelScope.launch {
            repository.sendNotification(pushNotification)
        }
    }

    private fun sendPrivateMessage(
        imageUrl: String = "",
        videoUrl: String = "",
        message: String,
        getterUsername: String,
        getterUserImage: String,
        getterUserId: String,
        getterToken: String,
        replyMessage: String? = null,
        replyImage: String? = null,
        replyVideo: String? = null
    ) {
        isLoading.value = true
        val userId = auth.currentUser?.uid
        val username = userData.value?.username
        val randomId = UUID.randomUUID().toString()

        val messageData = MessageData(
            imageUrl = imageUrl,
            videoUrl = videoUrl,
            message = message,
            messageId = randomId,
            senderUserId = userId,
            senderUserImage = userData.value?.image,
            senderUsername = username,
            getterUsername = getterUsername,
            getterUserImage = getterUserImage,
            getterUserId = getterUserId,
            getterToken = getterToken,
            replyMessage = replyMessage,
            replyImage = replyImage,
            replyVideo = replyVideo
        )

        firestore.collection("message").document(randomId).set(messageData).addOnSuccessListener {
            isLoading.value = false
        }

        firestore.collection("message")
            .whereEqualTo("messageId", randomId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val visibilityList =
                        document.get("visibility") as? ArrayList<String> ?: arrayListOf()

                    if (visibilityList.contains(userId)) {
                        visibilityList.remove(userId!!)
                        visibilityList.add(userId)
                        visibilityList.add(getterUserId)
                    } else {
                        visibilityList.add(userId!!)
                        visibilityList.add(getterUserId)
                    }

                    document.reference.update("visibility", visibilityList)
                }
            }

        firestore.collection("user")
            .whereEqualTo("userId", getterUserId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val gotMessagesFrom =
                        document.get("gotMsgFrom") as? ArrayList<String> ?: arrayListOf()

                    if (gotMessagesFrom.contains(userId)) {
                        gotMessagesFrom.remove(userId!!)
                        gotMessagesFrom.add(userId)
                    } else {
                        gotMessagesFrom.add(userId!!)
                    }

                    document.reference.update("gotMsgFrom", gotMessagesFrom)
                }
            }



        firestore.collection("user")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val sendMessagesTo = arrayListOf<String>()

                    userData.value?.sendMsgTo?.let {
                        sendMessagesTo.addAll(it)
                    }

                    if (sendMessagesTo.contains(getterUserId)) {
                        sendMessagesTo.remove(getterUserId)
                        sendMessagesTo.add(getterUserId)
                    } else {
                        sendMessagesTo.add(getterUserId)
                    }
                    document.reference.update("sendMsgTo", sendMessagesTo)
                }
            }
    }

    fun getPrivateMessages(getterUserId: String? = null) {
        val currentUserId = auth.currentUser?.uid
        val userId = userData.value?.userId

        if (currentUserId != null) {
            firestore.collection("message")
                .whereIn("senderUserId", listOf(userId, getterUserId))
                .whereIn("getterUserId", listOf(userId, getterUserId))
                .whereArrayContains("visibility", currentUserId)

                .addSnapshotListener { value, _ ->
                    value?.let { it ->
                        messagesData.value = it.toObjects<MessageData>().sortedBy { it.time }
                    }
                }
        }
    }

//    private fun getMessageData() {
//        val currentUserId = auth.currentUser?.uid
//        if (currentUserId != null) {
//            firestore.collection("user").
//                .addOnSuccessListener {
//                    userData.value = it.toObject<UserData>()
//                }
//        }
//    }

    fun getMessagesAsync() {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            firestore.collection("message")
                .addSnapshotListener { value, _ ->
                    value?.let { it ->
                        allMessagesAsync.value = it.toObjects<MessageData>().sortedBy { it.time }
                    }
                }
        }
    }

    fun getMessagesSync() {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            firestore.collection("message")
                .get()
                .addOnSuccessListener {
                    allMessagesSync.value = it.toObjects()
                }
        }
    }

    fun deleteMessageFromDatabase(messageId: String) {
        firestore.collection("message").document(messageId).delete()
    }

    fun deleteMessage(messageId: String, userId: String) {

        firestore.collection("message")
            .whereEqualTo("messageId", messageId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val visibilityList = arrayListOf<String>()

                    messageData.value?.visibility?.let {
                        visibilityList.addAll(it)
                    }

                    if (visibilityList.contains(userId)) {
                        visibilityList.remove(userId)
                        visibilityList.add(userId)
                    } else {
                        visibilityList.add(userId)
                    }
                    document.reference.update("visibility", visibilityList)
                }
            }
    }


    fun emoteMessage(messageId: String, myEmoji: String) {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            firestore.collection("message")
                .whereEqualTo("messageId", messageId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.update("messageIsEmoted", myEmoji)
                    }
                }
        }
    }

    fun deleteChat(userId: String) {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            firestore.collection("user").whereEqualTo("userId", currentUserId).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val gotMessagesFrom =
                            document.get("gotMsgFrom") as? ArrayList<String> ?: arrayListOf()

                        gotMessagesFrom.remove(userId)

                        document.reference.update("gotMsgFrom", gotMessagesFrom)
                    }
                    for (document in documents) {
                        val sendMessagesTo =
                            document.get("sendMsgTo") as? ArrayList<String> ?: arrayListOf()

                        sendMessagesTo.remove(userId)

                        document.reference.update("sendMsgTo", sendMessagesTo)
                    }
                }
        }
    }


    //Search Users
    fun searchList(query: String) {
        val listToSearch = if (isSearchStarting) {
            usersData.value
        } else {
            initialUsers
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                usersData.value = initialUsers
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.username!!.contains(
                    query.trim(),
                    ignoreCase = true
                )
            }
            if (isSearchStarting) {
                initialUsers = usersData.value
                isSearchStarting = false
            }
            usersData.value = results
        }
    }

    fun clearSearch() {
        usersData.value = initialUsers
        isSearchStarting = true
    }


}








