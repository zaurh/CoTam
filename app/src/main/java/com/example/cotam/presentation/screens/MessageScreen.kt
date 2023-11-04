package com.example.cotam.presentation.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.twotone.AttachFile
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material.icons.twotone.Reply
import androidx.compose.material.icons.twotone.VideoFile
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.cotam.R
import com.example.cotam.common.Constants.messagingUsernameNotification
import com.example.cotam.common.VideoPlayer
import com.example.cotam.common.ZoomableImg
import com.example.cotam.data.MessageData
import com.example.cotam.data.UserData
import com.example.cotam.presentation.components.MessageItem
import com.example.cotam.presentation.screens.viewmodel.MessageViewModel
import com.example.cotam.presentation.screens.viewmodel.UserViewModel
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    userData: UserData,
    navController: NavController,
    messageViewModel: MessageViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {

    messagingUsernameNotification = userData.username ?: ""
    val isMediaLoading = messageViewModel.isMediaLoading.value
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val messages = messageViewModel.privateMessagesData.value
    var messageTf by remember { mutableStateOf("") }
    var replyMessage = messageViewModel.replyingMessage.value
    var replyImage = messageViewModel.replyingImage.value
    var replyVideo = messageViewModel.replyingVideo.value
    val senderUserData = userViewModel.userData.value


    val selectedMessages = messageViewModel.selectedMessages

    val focus = FocusRequester()
    val focusManager = LocalFocusManager.current

    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    val context = LocalContext.current
    val contentResolver = context.contentResolver
    

    var expanded by remember { mutableStateOf(false) }
    var deleteAlert by remember { mutableStateOf(false) }


    val launcherMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            messageViewModel.sendMedia(
                uri = it,
                messageData = MessageData(
                    senderId = senderUserData?.userId,
                    getterId = userData.userId,
                    visibility = mutableListOf(
                        senderUserData?.userId ?: "",
                        userData.userId ?: ""
                    )
                ),
                contentResolver
            )
        }
    }


    LaunchedEffect(key1 = true) {
        scope.launch {
            scrollState.scrollToItem(messages.size + 99)
        }
    }

    senderUserData?.let {
        messageViewModel.getPrivateMessages(
            senderId = it.userId ?: "",
            getterId = userData.userId ?: ""
        )
    }


    var dialogState by remember { mutableStateOf(false) }

    if (dialogState) {
        Dialog(onDismissRequest = {
            dialogState = false
        }) {
            Box(modifier = Modifier.size(400.dp), Alignment.Center) {
                ZoomableImg(url = userData.image ?: "")
            }
        }
    }

    if (deleteAlert) {
        Dialog(onDismissRequest = {
            deleteAlert = false
        }) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10))
                    .background(Color.Gray)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Delete message?", color = Color.LightGray)
                Spacer(modifier = Modifier.size(20.dp))
                Button(
                    onClick = {
                        for (messageData in selectedMessages) {
                            if (messageData.visibility.size == 1) {
                                messageViewModel.deleteMessageFromDatabase(
                                    messageData.messageId ?: ""
                                )
                            } else {
                                if (messageData.senderId == senderUserData?.userId) {
                                    messageViewModel.deleteMessage(messageData)
                                } else {
                                    messageViewModel.deleteMessage(messageData.copy(senderId = senderUserData?.userId))
                                }
                            }
                        }
                        deleteAlert = false
                        selectedMessages.clear()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(text = "Delete")
                }

                var deleteDatabase by remember { mutableStateOf(false) }

                for (i in selectedMessages) {
                    if (i.getterId == userData.userId) {
                        deleteDatabase = true
                    }
                }

                if (deleteDatabase) {
                    Button(
                        onClick = {
                            for (i in selectedMessages) {
                                if (i.getterId == userData.userId) {
                                    messageViewModel.deleteMessageFromDatabase(i.messageId ?: "")
                                }
                            }
                            deleteAlert = false
                            selectedMessages.clear()
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(text = "Delete for everyone")
                    }
                }
                Button(
                    onClick = { deleteAlert = false }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(text = "Cancel")
                }
            }

        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    colorResource(id = R.color.blue)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedMessages.isEmpty()) {
                            navController.popBackStack()
                        } else {
                            selectedMessages.clear()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                },
                title = {
                    if (selectedMessages.isEmpty()) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(35.dp)
                                    .clickable {
                                        dialogState = true
                                    },
                                painter = rememberImagePainter(
                                    data = userData.image ?: "https://shorturl.at/jmoHM"
                                ),
                                contentDescription = ""
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(text = "${userData.username}")
                        }
                    } else {
                        Text(
                            text = selectedMessages.size.toString(),
                            modifier = Modifier.clickable {

                            })
                    }
                },
                actions = {
                    if (selectedMessages.isNotEmpty()) {
                        if (selectedMessages.size <= 1) {
                            IconButton(onClick = {
                                focusManager.clearFocus()
                                focus.requestFocus()
                                messageViewModel.isReplyingState.value = true
                                for (i in selectedMessages) {
                                    if (URLUtil.isValidUrl(i.imageUrl.toString())) {
                                        messageViewModel.replyingImage.value =
                                            (i.imageUrl ?: "null geldi") as String
                                    } else if (URLUtil.isValidUrl(i.videoUrl)) {
                                        messageViewModel.replyingVideo.value = i.videoUrl ?: "null geldi"
                                    } else {
                                        messageViewModel.replyingMessage.value = i.message ?: "null geldi"
                                    }
                                    messageViewModel.replyingPerson.value = i.senderUsername ?: ""
                                }
                                selectedMessages.removeAll(messages)
                            }) {
                                Icon(imageVector = Icons.Default.Reply, contentDescription = "")
                            }

                            for (i in selectedMessages) {
//                                if (i.imageUrl == "" && i.videoUrl == "") {
//                                    IconButton(onClick = {
//                                        clipboardManager.setText(AnnotatedString((i.message ?: "")))
//                                        Toast.makeText(
//                                            context,
//                                            "Message copied",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                        selectedMessages.removeAll(messages)
//                                    }) {
//                                        Icon(
//                                            imageVector = Icons.Default.ContentCopy,
//                                            contentDescription = ""
//                                        )
//                                    }
//                                }
                            }
                        }
                        IconButton(onClick = {
                            deleteAlert = true
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                        }

                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LazyColumn(state = scrollState, modifier = Modifier.weight(6f)) {
                    items(messages.takeLast(30)) {
                        MessageItem(messageData = it, onSwipe = {
                            focusManager.clearFocus()
                            focus.requestFocus()
                        })
                    }
                }
                if (isMediaLoading) {
                    Log.e("hehehe", "tru oldu")
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                            color = colorResource(id = R.color.green)
                        )
                    }
                }

                if (messageViewModel.isReplyingState.value) {
                    Row(
                        Modifier
                            .padding(start = 10.dp)
                            .align(Alignment.Start)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorResource(id = R.color.blue)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.padding(start = 10.dp),
                            imageVector = Icons.TwoTone.Reply,
                            contentDescription = ""
                        )
                        Column(
                            modifier = Modifier.padding(start = 10.dp)
                        ) {
                            Text(
                                text = messageViewModel.replyingPerson.value,
                                textAlign = TextAlign.Start,
                                color = Color.DarkGray
                            )
                            if (URLUtil.isValidUrl(messageViewModel.replyingImage.value)) {
                                Image(
                                    modifier = Modifier.size(50.dp),
                                    painter = rememberImagePainter(data = messageViewModel.replyingImage.value),
                                    contentDescription = ""
                                )

                            } else if (URLUtil.isValidUrl(messageViewModel.replyingVideo.value)) {
                                Box(modifier = Modifier.size(50.dp)) {
                                    VideoPlayer(
                                        url = messageViewModel.replyingVideo.value,
                                        autoPlay = false
                                    )
                                }
                            } else {
                                Text(
                                    text = messageViewModel.replyingMessage.value,
                                    textAlign = TextAlign.Start,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }


                        }
                        Spacer(modifier = Modifier.size(30.dp))
                        IconButton(onClick = {
                            messageViewModel.isReplyingState.value = false
                            messageViewModel.replyingMessage.value = ""
                        }) {
                            Icon(imageVector = Icons.TwoTone.Close, contentDescription = "")
                        }
                    }

                }
                TextField(
                    maxLines = 3,
                    modifier = Modifier
                        .focusRequester(focus)
                        .fillMaxWidth()
                        .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                        .clip(CircleShape),
                    colors = TextFieldDefaults.textFieldColors(
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    value = messageTf,
                    onValueChange = { messageTf = it },
                    trailingIcon = {
                        Row {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(onClick = {
                                    launcherMedia.launch("image/*")
                                    expanded = false
                                }) {
                                    Icon(imageVector = Icons.TwoTone.Image, contentDescription = "")
                                    Text(text = "Image")
                                }
                                DropdownMenuItem(onClick = {
                                    launcherMedia.launch("video/*")
                                    expanded = false
                                }) {
                                    Icon(
                                        imageVector = Icons.TwoTone.VideoFile,
                                        contentDescription = ""
                                    )
                                    Text(text = "Video")
                                }
                            }
                            IconButton(onClick = {
                                expanded = true
                            }) {
                                Icon(
                                    imageVector = Icons.TwoTone.AttachFile,
                                    contentDescription = ""
                                )
                            }
                            IconButton(onClick = {
                                if (messageTf.trim().isNotEmpty()) {
                                    scope.launch {
                                        if (messages.isNotEmpty()) {
                                            scrollState.scrollToItem(messages.size)
                                        }
                                    }
                                    senderUserData?.let {
                                        val getterUserId = userData.userId

                                        if (getterUserId !in it.sendMsgTo) {
                                            val updatedSendMsgTo =
                                                it.sendMsgTo.toMutableList().apply {
                                                    add(getterUserId ?: "")
                                                }
                                            val updatedUserData =
                                                it.copy(sendMsgTo = updatedSendMsgTo)
                                            userViewModel.updateUser(updatedUserData)
                                        }
                                        messageViewModel.gotMsgFrom(
                                            getterUserId = userData.userId ?: "",
                                            senderUserId = it.userId ?: ""
                                        )
                                    }
                                    messageViewModel.sendMessage(
                                        MessageData(
                                            message = messageTf.trimStart().trimEnd(),
                                            replyMessage = replyMessage,
                                            replyImage = replyImage,
                                            replyVideo = replyVideo,
                                            visibility = mutableListOf(
                                                senderUserData?.userId ?: "",
                                                userData.userId ?: ""
                                            ),
                                            getterId = userData.userId,
                                            getterUsername = userData.username,
                                            getterImage = userData.image,
                                            senderId = senderUserData?.userId,
                                            senderUsername = senderUserData?.username,
                                            senderImage = senderUserData?.image
                                        )
                                    )

                                    messageTf = ""
                                    messageViewModel.isReplyingState.value = false
                                    messageViewModel.replyingMessage.value = ""
                                    messageViewModel.replyingImage.value = ""
                                    messageViewModel.replyingVideo.value = ""
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = "")
                            }
                        }
                    }
                )

            }


        },
    )

}
