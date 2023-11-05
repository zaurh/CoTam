package com.example.cotam.presentation.components

import android.webkit.URLUtil
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.twotone.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.example.cotam.R
import com.example.cotam.common.VideoPlayer
import com.example.cotam.common.ZoomableImg
import com.example.cotam.common.myTime
import com.example.cotam.data.remote.MessageData
import com.example.cotam.presentation.screens.viewmodel.MessageViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.auth.FirebaseAuth
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageItem(
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    messageData: MessageData,
    messageViewModel: MessageViewModel = hiltViewModel(),
    onSwipe: () -> Unit
) {
    var imageDialogState by remember { mutableStateOf(false) }
    var videoDialogState by remember { mutableStateOf(false) }

    val senderUid = messageData.senderId
    val currentUid = auth.currentUser?.uid

    var senderSide by remember { mutableStateOf(false) }

    val selectedMessages = messageViewModel.selectedMessages

    val context = LocalContext.current

    var emojiPickerState by rememberSaveable { mutableStateOf(false) }
    if (selectedMessages.isEmpty() || selectedMessages.size > 1) {
        emojiPickerState = false
    }

    LaunchedEffect(key1 = true) {
        if (senderUid == currentUid) {
            senderSide = true
        }
    }


    val reply = SwipeAction(
        icon = rememberVectorPainter(image = Icons.TwoTone.Reply),
        background = Color.Transparent,
        onSwipe = {
            onSwipe()
            messageViewModel.isReplyingState.value = true
            if (URLUtil.isValidUrl(messageData.imageUrl)) {
                messageViewModel.replyingMessage.value = ""
                messageViewModel.replyingVideo.value = ""
                messageViewModel.replyingImage.value = (messageData.imageUrl ?: "null")
            } else if (URLUtil.isValidUrl(messageData.videoUrl)) {
                messageViewModel.replyingMessage.value = ""
                messageViewModel.replyingImage.value = ""
                messageViewModel.replyingVideo.value = messageData.videoUrl ?: "null"
            } else {
                messageViewModel.replyingImage.value = ""
                messageViewModel.replyingVideo.value = ""
                messageViewModel.replyingMessage.value = messageData.message ?: "null"
            }
            messageViewModel.replyingPerson.value = messageData.senderUsername ?: "null"
        }
    )


    SwipeableActionsBox(
        swipeThreshold = 10.dp,
        backgroundUntilSwipeThreshold = Color.Transparent,
        startActions = listOf(reply),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        if (selectedMessages.contains(messageData)) {
                            Color.LightGray
                        } else {
                            Color.Transparent
                        }
                    )
                    .combinedClickable(
                        onClick = {
                            if (selectedMessages.isNotEmpty()) {
                                if (selectedMessages.contains(messageData)) {
                                    selectedMessages.remove(messageData)
                                } else {
                                    selectedMessages.add(messageData)
                                }
                            }
                        },
                        onLongClick = {
                            emojiPickerState = selectedMessages.isEmpty()
                            if (selectedMessages.contains(messageData)) {
                                selectedMessages.remove(messageData)
                            } else {
                                selectedMessages.add(messageData)
                            }
                        },
                    )
                    .padding(10.dp),
                horizontalAlignment = if (senderUid == currentUid) Alignment.End else Alignment.Start
            ) {
                if (imageDialogState) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Dialog(
                            properties = DialogProperties(usePlatformDefaultWidth = false),
                            onDismissRequest = {
                                imageDialogState = false
                            }) {
                            ZoomableImg(url = (messageData.imageUrl ?: ""))
                        }
                    }
                }
                if (videoDialogState) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val player = SimpleExoPlayer.Builder(context).build()
                        val playerView = PlayerView(context)
                        val mediaItem = MediaItem.fromUri(messageData.videoUrl ?: "")

                        Dialog(
                            properties = DialogProperties(usePlatformDefaultWidth = false),
                            onDismissRequest = {
                                videoDialogState = false
                                player.stop()
                            }) {
                            player.setMediaItem(mediaItem)
                            playerView.player = player
                            playerView.useController = true
                            LaunchedEffect(player) {
                                player.prepare()
                                player.playWhenReady = true
                            }
                            AndroidView(factory = {
                                playerView
                            })
                        }
                    }
                }
                AnimatedVisibility(
                    visible = emojiPickerState
                ) {
                    if (selectedMessages.size == 1) {
                        if (messageData.senderId != auth.uid) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "\uD83D\uDC4D\uD83C\uDFFB",
                                    fontSize = 26.sp,
                                    modifier = Modifier.clickable {
                                        messageViewModel.emoteMessage(
                                            messageData.messageId ?: "",
                                            "\uD83D\uDC4D\uD83C\uDFFB"
                                        )
                                        selectedMessages.clear()
                                        emojiPickerState = false
                                    })
                                Text(text = "❤️", fontSize = 26.sp, modifier = Modifier.clickable {
                                    messageViewModel.emoteMessage(messageData.messageId ?: "", "❤️")
                                    selectedMessages.clear()
                                    emojiPickerState = false

                                })
                                Text(
                                    text = "\uD83D\uDE02",
                                    fontSize = 26.sp,
                                    modifier = Modifier.clickable {
                                        messageViewModel.emoteMessage(
                                            messageData.messageId ?: "",
                                            "\uD83D\uDE02"
                                        )
                                        selectedMessages.clear()
                                        emojiPickerState = false

                                    })
                                Text(
                                    text = "\uD83D\uDE22",
                                    fontSize = 26.sp,
                                    modifier = Modifier.clickable {
                                        messageViewModel.emoteMessage(
                                            messageData.messageId ?: "",
                                            "\uD83D\uDE22"
                                        )
                                        selectedMessages.clear()
                                        emojiPickerState = false

                                    })
                                Text(
                                    text = "\uD83D\uDE21",
                                    fontSize = 26.sp,
                                    modifier = Modifier.clickable {
                                        messageViewModel.emoteMessage(
                                            messageData.messageId ?: "",
                                            "\uD83D\uDE21"
                                        )
                                        selectedMessages.clear()
                                        emojiPickerState = false

                                    })
                            }
                        }

                    }
                }


                if (messageData.videoUrl?.isNotEmpty() == true) {
                    Box(modifier = Modifier.size(200.dp)) {
                        Column(
                            modifier = Modifier
                                .size(200.dp)
                                .combinedClickable(
                                    onClick = {
                                        if (selectedMessages.isNotEmpty()) {
                                            emojiPickerState = !emojiPickerState
                                            if (selectedMessages.contains(messageData)) {
                                                selectedMessages.remove(messageData)
                                            } else {
                                                selectedMessages.add(messageData)
                                            }
                                        } else {
                                            videoDialogState = true
                                        }
                                    },
                                    onLongClick = {
                                        emojiPickerState = !emojiPickerState
                                        if (selectedMessages.contains(messageData)) {
                                            selectedMessages.remove(messageData)
                                        } else {
                                            selectedMessages.add(messageData)
                                        }

                                    }
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            VideoPlayer(url = messageData.videoUrl ?: "", autoPlay = false)
                        }
                        Column(
                            Modifier.size(200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(50.dp)
                                    .background(color = colorResource(id = R.color.videoPlayer)),
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "",
                                tint = Color.White
                            )

                        }

                    }

                }
                if (messageData.imageUrl?.isNotEmpty() == true) {
                    Image(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50f))
                            .size(150.dp)
                            .combinedClickable(
                                onClick = {
                                    if (selectedMessages.isNotEmpty()) {
                                        emojiPickerState = !emojiPickerState
                                        if (selectedMessages.contains(messageData)) {
                                            selectedMessages.remove(messageData)
                                        } else {
                                            selectedMessages.add(messageData)
                                        }
                                    } else {
                                        imageDialogState = true
                                    }
                                },
                                onLongClick = {
                                    emojiPickerState = !emojiPickerState
                                    if (selectedMessages.contains(messageData)) {
                                        selectedMessages.remove(messageData)
                                    } else {
                                        selectedMessages.add(messageData)
                                    }

                                }
                            ),
                        painter = rememberImagePainter(data = messageData.imageUrl),
                        contentDescription = ""
                    )
                }
                if (messageData.message?.isNotEmpty() == true) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (messageData.replyMessage != "" || messageData.replyImage != "" || messageData.replyVideo != "") {
                            Icon(
                                imageVector = Icons.TwoTone.Reply,
                                contentDescription = "",
                                modifier = Modifier.size(20.dp)
                            )

                            if (URLUtil.isValidUrl(messageData.replyImage)) {
                                Image(
                                    modifier = Modifier.size(50.dp),
                                    painter = rememberImagePainter(data = messageData.replyImage),
                                    contentDescription = ""
                                )
                            } else if (URLUtil.isValidUrl(messageData.replyVideo)) {
                                Box(modifier = Modifier.size(50.dp)) {
                                    VideoPlayer(
                                        url = messageData.replyVideo ?: "",
                                        autoPlay = false
                                    )
                                }
                            } else {
                                Text(
                                    text = messageData.replyMessage ?: "",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colorResource(id = R.color.gray))
                                        .padding(10.dp),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                        }
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (senderUid == currentUid) colorResource(id = R.color.green) else colorResource(
                                    id = R.color.blueStatus
                                )
                            )
                            .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp),
                        horizontalAlignment = if (senderUid == currentUid) Alignment.End else Alignment.Start
                    ) {
                        Text(text = messageData.message ?: "", color = Color.White)
                    }

                }
                Row() {
                    Text(text = messageData.messageIsEmoted ?: "")
                    Text(text = myTime(messageData.time!!), fontSize = 12.sp)
                }
            }
        }
    }


}