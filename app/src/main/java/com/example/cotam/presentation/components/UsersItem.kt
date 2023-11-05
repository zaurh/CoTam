package com.example.cotam.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
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
import com.example.cotam.data.remote.UserData
import com.example.cotam.presentation.screens.viewmodel.MessageViewModel
import com.example.cotam.presentation.screens.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UsersItem(
    userData: UserData,
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel(),
    messageViewModel: MessageViewModel = hiltViewModel(),
    showAllUsers: Boolean,
    showLastMessage: Boolean
) {

    var dialogState by remember { mutableStateOf(false) }
    val showListState = remember { mutableStateOf(showAllUsers) }
    val currentUser = userViewModel.userData.value
    val messages = messageViewModel.allMessages


    if (showListState.value) {
        Row(
            modifier = Modifier
                .clickable {
                    navigateTo(navController, "message", NavParam("userData", userData))
                }
                .fillMaxWidth()
                .padding(10.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            if (dialogState) {
                Dialog(onDismissRequest = {
                    dialogState = false
                }) {
                    Box(modifier = Modifier.size(400.dp), Alignment.Center) {
                        ZoomableImg(url = userData.image ?: "")
                    }
                }
            }
            Image(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (userData.image?.isNotEmpty() == true) {
                            dialogState = true
                        }
                    },
                contentScale = ContentScale.Crop,
                painter = rememberImagePainter(
                    data = userData.image ?: "https://shorturl.at/jmoHM"
                ),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.size(10.dp))

            Column {

                Text(text = userData.username ?: "", fontSize = 18.sp)

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
                            if (i.getterId == userData.userId && i.senderId == auth.currentUser?.uid ||
                                i.senderId == userData.userId && i.getterId == auth.currentUser?.uid
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