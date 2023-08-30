package com.example.cotam.presentation.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.URLUtil
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
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
import androidx.compose.ui.text.AnnotatedString
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
import com.example.cotam.data.UserData
import com.example.cotam.presentation.SharedViewModel
import com.example.cotam.presentation.components.MessageItem
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    viewModel: SharedViewModel = hiltViewModel(),
    userData: UserData,
    navController: NavController
) {

    messagingUsernameNotification = userData.username ?: ""
    val isImageLoading = viewModel.isImageLoading.value
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val messages = viewModel.messagesData.value
    var messageTf by remember { mutableStateOf("") }
    var replyMessage = viewModel.replyingMessage.value
    var replyImage = viewModel.replyingImage.value
    var replyVideo = viewModel.replyingVideo.value


    var allMessages by remember { mutableStateOf(false) }
    val selectedMessages = viewModel.selectedMessages

    val focus = FocusRequester()
    val focusManager = LocalFocusManager.current

    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    val context = LocalContext.current


    var expanded by remember { mutableStateOf(false) }
    var deleteAlert by remember { mutableStateOf(false) }


    val launcherImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.sendImage(
                uri = it,
                message = messageTf,
                getterUserId = userData.userId ?: "",
                getterUsername = userData.username ?: "",
                getterUserImage = userData.image ?: "",
                getterToken = userData.token ?: ""
            )
        }
    }

    val launcherVideo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.sendVideo(
                uri = it,
                message = messageTf,
                getterUserId = userData.userId ?: "",
                getterUsername = userData.username ?: "",
                getterUserImage = userData.image ?: "",
                getterToken = userData.token ?: ""
            )
        }
    }


    LaunchedEffect(key1 = true) {
        allMessages = true
        scope.launch {
            scrollState.scrollToItem(messages.size + 99)
        }
    }

    if (allMessages) {

        viewModel.getPrivateMessages(userData.userId ?: "")
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
                        for (i in selectedMessages) {
                            if (i.visibility.size == 1) {
                                viewModel.deleteMessageFromDatabase(i.messageId ?: "")
                            } else {
                                if (i.getterUserId == userData.userId) {
                                    viewModel.deleteMessage(i.messageId ?: "", i.getterUserId ?: "")
                                } else {
                                    viewModel.deleteMessage(i.messageId ?: "", i.senderUserId ?: "")
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
                    if (i.getterUserId == userData.userId) {
                        deleteDatabase = true
                    }
                }

                if (deleteDatabase) {
                    Button(
                        onClick = {
                            for (i in selectedMessages) {
                                if (i.getterUserId == userData.userId) {
                                    viewModel.deleteMessageFromDatabase(i.messageId ?: "")
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
                        if (selectedMessages.isEmpty()){
                            navController.popBackStack()
                        }else{
                            selectedMessages.clear()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                },
                title = {
                    if (selectedMessages.isEmpty()){
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                    }else{
                        Text(text = selectedMessages.size.toString(), modifier = Modifier.clickable {

                        })
                    }
                },
                actions = {
                    if (selectedMessages.isNotEmpty()) {
                        if (selectedMessages.size <= 1) {
                            IconButton(onClick = {
                                focusManager.clearFocus()
                                focus.requestFocus()
                                viewModel.isReplyingState.value = true
                                for (i in selectedMessages) {
                                    if (URLUtil.isValidUrl(i.imageUrl)) {
                                        viewModel.replyingImage.value = i.imageUrl ?: "null geldi"
                                    } else if (URLUtil.isValidUrl(i.videoUrl)) {
                                        viewModel.replyingVideo.value = i.videoUrl ?: "null geldi"
                                    } else {
                                        viewModel.replyingMessage.value = i.message ?: "null geldi"
                                    }
                                    viewModel.replyingPerson.value = i.senderUsername ?: ""
                                }
                                selectedMessages.removeAll(messages)
                            }) {
                                Icon(imageVector = Icons.Default.Reply, contentDescription = "")
                            }

                            for (i in selectedMessages) {
                                if (i.imageUrl == "" && i.videoUrl == "") {
                                    IconButton(onClick = {
                                        clipboardManager.setText(AnnotatedString((i.message ?: "")))
                                        Toast.makeText(
                                            context,
                                            "Message copied",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        selectedMessages.removeAll(messages)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = ""
                                        )
                                    }
                                }
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
                    .padding(top = 70.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LazyColumn(state = scrollState, modifier = Modifier.weight(6f)) {
                    items(messages.takeLast(30)) {
                        MessageItem(messageData = it,onSwipe = {
                            focusManager.clearFocus()
                            focus.requestFocus()
                        })
                    }
                }
                if (isImageLoading) {
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

                if (viewModel.isReplyingState.value) {
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
                                text = viewModel.replyingPerson.value,
                                textAlign = TextAlign.Start,
                                color = Color.DarkGray
                            )
                            if (URLUtil.isValidUrl(viewModel.replyingImage.value)) {
                                Image(
                                    modifier = Modifier.size(50.dp),
                                    painter = rememberImagePainter(data = viewModel.replyingImage.value),
                                    contentDescription = ""
                                )

                            } else if (URLUtil.isValidUrl(viewModel.replyingVideo.value)) {
                                Box(modifier = Modifier.size(50.dp)) {
                                    VideoPlayer(
                                        url = viewModel.replyingVideo.value,
                                        autoPlay = false
                                    )
                                }
                            } else {
                                Text(
                                    text = viewModel.replyingMessage.value,
                                    textAlign = TextAlign.Start,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }


                        }
                        Spacer(modifier = Modifier.size(30.dp))
                        IconButton(onClick = {
                            viewModel.isReplyingState.value = false
                            viewModel.replyingMessage.value = ""
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
                                    launcherImage.launch("image/*")
                                    expanded = false
                                }) {
                                    Icon(imageVector = Icons.TwoTone.Image, contentDescription = "")
                                    Text(text = "Image")
                                }
                                DropdownMenuItem(onClick = {
                                    launcherVideo.launch("video/*")
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
                                    viewModel.sendMessage(
                                        message = messageTf.trimStart().trimEnd(),
                                        getterUsername = userData.username ?: "",
                                        getterUserImage = userData.image ?: "",
                                        getterUserId = userData.userId ?: "",
                                        getterToken = userData.token ?: "",
                                        replyMessage = replyMessage,
                                        replyImage = replyImage,
                                        replyVideo = replyVideo
                                    )
                                    messageTf = ""
                                    viewModel.isReplyingState.value = false
                                    viewModel.replyingMessage.value = ""
                                    viewModel.replyingImage.value = ""
                                    viewModel.replyingVideo.value = ""
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
