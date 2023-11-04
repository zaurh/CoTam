package com.example.cotam.presentation.screens.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cotam.data.MessageData
import com.example.cotam.data.UserData
import com.example.cotam.data.repository.MessageRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepo: MessageRepo
) : ViewModel(){

    val userData = messageRepo.userData
    val privateMessagesData = messageRepo.privateMessagesData
    val allMessages = messageRepo.allMessages
    val selectedMessages = mutableStateListOf<MessageData>()

    var replyingPerson = mutableStateOf("")
    var replyingMessage = mutableStateOf("")
    var replyingImage = mutableStateOf("")
    var replyingVideo = mutableStateOf("")
    var isReplyingState = mutableStateOf(false)

    val isMessageLoading = messageRepo.isMessageLoading
    val isMediaLoading = messageRepo.isMediaLoading

    fun sendMedia(
        uri: Uri,
        messageData: MessageData,
        contentResolver: ContentResolver
    ) {
        messageRepo.sendMedia(
            uri, messageData, contentResolver
        )
    }


    fun sendMessage(
        messageData: MessageData,
    ) {
        messageRepo.sendMessage(
            messageData
        )
    }


    fun getPrivateMessages(senderId: String, getterId: String) {
        messageRepo.getPrivateMessages(senderId, getterId)
    }

    fun deleteMessageFromDatabase(messageId: String) {
        messageRepo.deleteMessageFromDatabase(messageId)
    }

    fun deleteMessage(messageData: MessageData) {
        messageRepo.deleteMessage(messageData)
    }


    fun emoteMessage(messageId: String, myEmoji: String) {
        messageRepo.emoteMessage(messageId, myEmoji)
    }

    //getterUser take senderUserId to gotMsgFrom
    fun gotMsgFrom(
        getterUserId: String,
        senderUserId:String
    ){
        messageRepo.gotMsgFrom(getterUserId, senderUserId)
    }
}