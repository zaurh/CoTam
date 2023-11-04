package com.example.cotam.data.repository

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.example.cotam.common.isImageFile
import com.example.cotam.common.isVideoFile
import com.example.cotam.data.MessageData
import com.example.cotam.data.UserData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import javax.inject.Inject

class MessageRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userRepo: UserRepo,
    private val storageRepo: StorageRepo,
    private val authRepo: AuthRepo
) {

    val currentUserId: StateFlow<String?> = authRepo.currentUserId

    val userData = userRepo.userData.value

    val messageData = mutableStateOf<MessageData?>(null)
    val privateMessagesData = mutableStateOf<List<MessageData>>(emptyList())
    val allMessages = mutableStateOf<List<MessageData>>(emptyList())

    val isMessageLoading = mutableStateOf(false)
    val isMediaLoading = storageRepo.isMediaLoading

    init {
        getAllMessages()
    }

    fun sendMedia(
        uri: Uri,
        messageData: MessageData,
        contentResolver: ContentResolver
    ) {
        val isImage = isImageFile(uri, contentResolver)
        val isVideo = isVideoFile(uri, contentResolver)

        storageRepo.uploadMedia(uri, if (isImage) "images" else if (isVideo) "videos" else "other") { uploadedUri ->
            sendMessage(
                messageData = if (isImage) {
                    messageData.copy(imageUrl = uploadedUri.toString())
                } else if (isVideo) {
                    messageData.copy(videoUrl = uploadedUri.toString())
                } else {
                    messageData
                }
            )
        }
    }



    fun sendMessage(
        messageData: MessageData
    ) {

        sendPrivateMessage(
            messageData
        )
//        val notificationData = NotificationData(
//            title = userData.value?.username ?: "",
//            text = message
//        )

//        val pushNotification = PushNotification(data = notificationData, to = getterToken)
//        viewModelScope.launch {
//            repository.sendNotification(pushNotification)
//        }
    }

    private fun sendPrivateMessage(
        messageData: MessageData
    ) {
        isMessageLoading.value = true
        val randomId = UUID.randomUUID().toString()
        val msgData = messageData.copy(
            messageId = randomId
        )
        firestore.collection("message").document(randomId).set(msgData).addOnSuccessListener {
            isMessageLoading.value = false
        }
    }

    fun gotMsgFrom(
        getterUserId: String,
        senderUserId: String
    ) {
        firestore.collection("user")
            .whereEqualTo("userId", getterUserId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val gotMessagesFrom =
                        document.get("gotMsgFrom") as? ArrayList<String> ?: arrayListOf()

                    if (gotMessagesFrom.contains(senderUserId)) {
                        gotMessagesFrom.remove(senderUserId)
                        gotMessagesFrom.add(senderUserId)
                    } else {
                        gotMessagesFrom.add(senderUserId)
                    }

                    document.reference.update("gotMsgFrom", gotMessagesFrom)
                }
            }
    }

    fun getPrivateMessages(senderId: String, getterId: String) {
        firestore.collection("message")
            .whereIn("senderId", listOf(senderId, getterId))
            .whereIn("getterId", listOf(senderId, getterId))
            .whereArrayContains("visibility", senderId)
            .addSnapshotListener { value, _ ->
                value?.let { it ->
                    privateMessagesData.value = it.toObjects<MessageData>().sortedBy { it.time }
                }
            }
    }

    fun getAllMessages(){
        firestore.collection("message")
            .addSnapshotListener { value, _ ->
                value?.let { it ->
                    allMessages.value = it.toObjects<MessageData>().sortedBy { it.time }
                }
            }
    }


    fun deleteMessageFromDatabase(messageId: String) {
        firestore.collection("message").document(messageId).delete()
    }

    private fun updateMessage(messageData: MessageData) {
        val msgData = messageData.copy(
            message = messageData.message,
            visibility = messageData.visibility
        )
        firestore.collection("message").document(messageData.messageId ?: "")
            .update(msgData.toMap())
    }


    fun deleteMessage(messageData: MessageData) {
        val senderUserId = messageData.senderId

        if (senderUserId in messageData.visibility) {
            val updatedVisibility = messageData.visibility.toMutableList().apply {
                remove(senderUserId ?: "")
            }
            val updatedMessageData = messageData.copy(visibility = updatedVisibility)
            updateMessage(updatedMessageData)
        }
    }


    fun emoteMessage(messageId: String, myEmoji: String) {
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