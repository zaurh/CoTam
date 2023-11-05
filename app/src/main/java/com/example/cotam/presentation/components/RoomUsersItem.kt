package com.example.cotam.presentation.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.cotam.common.NavParam
import com.example.cotam.common.VideoPlayer
import com.example.cotam.common.ZoomableImg
import com.example.cotam.common.myTime
import com.example.cotam.common.navigateTo
import com.example.cotam.data.local.UserEntity
import com.example.cotam.data.remote.UserData
import com.example.cotam.data.toUserData
import com.example.cotam.presentation.screens.viewmodel.AuthViewModel
import com.example.cotam.presentation.screens.viewmodel.MessageViewModel
import com.example.cotam.presentation.screens.viewmodel.RoomViewModel
import com.example.cotam.presentation.screens.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomUsersItem(
    userEntity: UserEntity,
    navController: NavController,
    messageViewModel: MessageViewModel = hiltViewModel(),
    showAllUsers: Boolean,
    showLastMessage: Boolean,
    roomViewModel: RoomViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var dialogState by remember { mutableStateOf(false) }
    val showListState = remember { mutableStateOf(showAllUsers) }
    val messages = messageViewModel.allMessages
    val currentUserId = authViewModel.currentUserId.collectAsState()
    val selectedUsers = roomViewModel.selectedUsers




    if (showListState.value) {
        Row(
            modifier = Modifier
                .background(
                    if (selectedUsers.contains(userEntity)) {
                        Color.LightGray
                    } else {
                        Color.Transparent
                    }
                )
                .combinedClickable(
                    onClick = {
                        if (selectedUsers.isNotEmpty()) {
                            if (selectedUsers.contains(userEntity)) {
                                selectedUsers.remove(userEntity)
                            } else {
                                selectedUsers.add(userEntity)
                            }
                        }else{
                            navigateTo(
                                navController,
                                "message",
                                NavParam("userData", userEntity.toUserData())
                            )
                        }
                    },
                    onLongClick = {
                        if (selectedUsers.contains(userEntity)) {
                            selectedUsers.remove(userEntity)
                        } else {
                            selectedUsers.add(userEntity)
                        }
                    },
                )
                .fillMaxWidth()
                .padding(10.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            if (dialogState) {
                Dialog(onDismissRequest = {
                    dialogState = false
                }) {
                    Box(modifier = Modifier.size(400.dp), Alignment.Center) {
                        ZoomableImg(url = userEntity.userImage ?: "")
                    }
                }
            }

            Image(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .combinedClickable(
                        onClick = {
                            if (selectedUsers.isNotEmpty()) {
                                if (selectedUsers.contains(userEntity)) {
                                    selectedUsers.remove(userEntity)
                                } else {
                                    selectedUsers.add(userEntity)
                                }
                            }else{
                                if (userEntity.userImage?.isNotEmpty() == true) {
                                    dialogState = true
                                }
                            }
                        },
                        onLongClick = {
                            if (selectedUsers.contains(userEntity)) {
                                selectedUsers.remove(userEntity)
                            } else {
                                selectedUsers.add(userEntity)
                            }
                        },
                    ),
                contentScale = ContentScale.Crop,
                painter = rememberImagePainter(
                    data = userEntity.userImage ?: "https://shorturl.at/jmoHM"
                ),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.size(10.dp))

            Column {

                Text(text = userEntity.username ?: "", fontSize = 18.sp)

                if (showLastMessage) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var lastMsg = ""
                        var lastImg = ""
                        var lastVideo = ""
                        var lastMsgTime = ""

                        for (i in messages.value) {
                            if (i.getterId == userEntity.userId && i.senderId == currentUserId.value ||
                                i.senderId == userEntity.userId && i.getterId == currentUserId.value
                            ) {
                                lastMsg = i.message ?: ""
                                lastImg = i.imageUrl ?: ""
                                lastVideo = i.videoUrl ?: ""
                                lastMsgTime = myTime(i.time!!)
                            }
                        }
                        if (lastMsg.isNotEmpty()) {
                            Text(
                                text = lastMsg,
                                color = Color.DarkGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(150.dp)
                            )
                        } else if (lastImg.isNotEmpty()) {
                            Row {
                                Image(
                                    modifier = Modifier.size(24.dp),
                                    painter = rememberImagePainter(data = lastImg),
                                    contentDescription = ""
                                )
                                Spacer(modifier = Modifier.size(5.dp))
                                Text(text = "Photo", color = Color.Gray)
                            }
                        }
                        else if (lastVideo.isNotEmpty()) {
                            Row {
                                Box(modifier = Modifier.size(24.dp)){
                                    VideoPlayer(url = lastVideo, autoPlay = false)
                                }
                                Spacer(modifier = Modifier.size(5.dp))
                                Text(text = "Video", color = Color.Gray)
                            }
                        }

                        Text(text = lastMsgTime, color = Color.DarkGray, fontSize = 14.sp)
                    }

                }


            }


        }
    }
}