package com.zaurh.cotam.presentation.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.zaurh.cotam.R
import com.zaurh.cotam.common.Constants.messagingUsernameNotification
import com.zaurh.cotam.common.VideoPlayer
import com.zaurh.cotam.common.ZoomableImg
import com.zaurh.cotam.data.remote.MessageData
import com.zaurh.cotam.data.remote.NotificationData
import com.zaurh.cotam.data.remote.PushNotification
import com.zaurh.cotam.data.remote.UserData
import com.zaurh.cotam.presentation.components.MessageItem
import com.zaurh.cotam.presentation.screens.viewmodel.MessageViewModel
import com.zaurh.cotam.presentation.screens.viewmodel.UserViewModel
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    userData: UserData,
    navController: NavController,
    messageViewModel: MessageViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {

    messagingUsernameNotification = userData.username ?: ""
    val isMediaLoading = messageViewModel.isMediaLoading.value
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val messages = messageViewModel.privateMessagesData.value
    var messageTf by remember { mutableStateOf("") }
    val replyMessage = messageViewModel.replyingMessage.value
    val replyImage = messageViewModel.replyingImage.value
    val replyVideo = messageViewModel.replyingVideo.value
    val senderUserData = userViewModel.userData.value

    val selectedMessages = messageViewModel.selectedMessages

    val focus = FocusRequester()
    val focusManager = LocalFocusManager.current

    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    val context = LocalContext.current
    val contentResolver = context.contentResolver


    var expanded by remember { mutableStateOf(false) }
    var deleteAlert by remember { mutableStateOf(false) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    val imageCropLauncher =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let {
                    //getBitmap method is deprecated in Android SDK 29 or above so we need to do this check here
                    bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder
                            .createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                    it.let {
                        messageViewModel.sendMedia(
                            uri = it,
                            messageData = MessageData(
                                senderUsername = senderUserData?.username,
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
            } else {
                println("ImageCropping error: ${result.error}")
            }
        }

    val launcherMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            messageViewModel.sendMedia(
                uri = it,
                messageData = MessageData(
                    senderUsername = senderUserData?.username,
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
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Delete message?", color = MaterialTheme.colorScheme.primary)
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
                    Text(text = "Delete", color = MaterialTheme.colorScheme.secondary)
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
                        Text(
                            text = "Delete for everyone",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Button(
                    onClick = { deleteAlert = false }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(text = "Cancel", color = MaterialTheme.colorScheme.secondary)
                }
            }

        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedMessages.isEmpty()) {
                            navController.popBackStack()
                        } else {
                            selectedMessages.clear()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                                        if (userData.image != null) {
                                            dialogState = true
                                        }
                                    },
                                painter = rememberImagePainter(
                                    data = userData.image ?: "https://i.hizliresim.com/x7e0wpo.png"
                                ),
                                contentDescription = ""
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = "${userData.username}",
                                color = MaterialTheme.colorScheme.primary
                            )
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
                                            (i.imageUrl ?: "")
                                    } else if (URLUtil.isValidUrl(i.videoUrl)) {
                                        messageViewModel.replyingVideo.value =
                                            i.videoUrl ?: ""
                                    } else {
                                        messageViewModel.replyingMessage.value =
                                            i.message ?: ""
                                    }
                                    messageViewModel.replyingPerson.value = i.senderUsername ?: ""
                                }
                                selectedMessages.removeAll(messages)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.reply),
                                    contentDescription = ""
                                )
                            }
                            for (i in selectedMessages) {
                                if (i.imageUrl == null && i.videoUrl == null) {
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
                                            painter = painterResource(id = R.drawable.copy),
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
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Divider()
                LazyColumn(
                    state = scrollState, modifier = Modifier
                        .weight(6f)
                        .imePadding()
                ) {
                    items(messages.takeLast(30)) {
                        MessageItem(messageData = it, onSwipe = {
                            focusManager.clearFocus()
                            focus.requestFocus()
                        })
                    }
                }
                if (isMediaLoading) {
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
                            .background(MaterialTheme.colorScheme.tertiary),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.padding(start = 10.dp),
                            painter = painterResource(id = R.drawable.reply),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Column(
                            modifier = Modifier.padding(start = 10.dp)
                        ) {
                            Text(
                                text = messageViewModel.replyingPerson.value,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.secondary
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
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(30.dp))
                        IconButton(onClick = {
                            messageViewModel.isReplyingState.value = false
                            messageViewModel.replyingMessage.value = ""
                        }) {
                            Icon(
                                imageVector = Icons.TwoTone.Close,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                }


            }


        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.onBackground
            ) {
                TextField(
                    maxLines = 3,
                    modifier = Modifier
                        .focusRequester(focus)
                        .fillMaxWidth()
                        .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                        .clip(CircleShape),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
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
                                DropdownMenuItem(
                                    onClick = {
                                        val cropOptions = CropImageContractOptions(
                                            null,
                                            CropImageOptions(imageSourceIncludeCamera = false)
                                        )
                                        imageCropLauncher.launch(cropOptions)
                                        expanded = false
                                    },
                                    text = {
                                        Text(text = "Image")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painterResource(id = R.drawable.image),
                                            contentDescription = "",
                                            modifier = Modifier.size(22.dp),
                                            tint = colorResource(id = R.color.green)
                                        )
                                    }
                                )
                                DropdownMenuItem(onClick = {
                                    launcherMedia.launch("video/*")
                                    expanded = false
                                }, text = { Text(text = "Video") }, leadingIcon = {
                                    Icon(
                                        painterResource(id = R.drawable.video),
                                        contentDescription = "",
                                        modifier = Modifier.size(22.dp),
                                        tint = colorResource(id = R.color.green)
                                    )
                                })
                            }
                            IconButton(onClick = {
                                expanded = true
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.attach_file),
                                    contentDescription = ""
                                )
                            }
                            IconButton(onClick = {
                                if (messageTf.trim().isNotEmpty()) {
                                    val notificationData = NotificationData(
                                        title = senderUserData?.username ?: "",
                                        text = messageTf
                                    )

                                    val pushNotification = PushNotification(
                                        data = notificationData,
                                        to = userData.token ?: ""
                                    )

                                    scope.launch {
                                        messageViewModel.sendNotificiation(
                                            pushNotification
                                        )
                                        if (messages.isNotEmpty()) {
                                            scrollState.scrollToItem(messages.size)
                                        }
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
                                Icon(
                                    painter = painterResource(id = R.drawable.send),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                )
            }

        }
    )

}
