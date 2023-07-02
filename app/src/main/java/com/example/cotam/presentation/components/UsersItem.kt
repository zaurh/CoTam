package com.example.cotam.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.cotam.R
import com.example.cotam.common.NavParam
import com.example.cotam.common.ZoomableImg
import com.example.cotam.common.myTime
import com.example.cotam.common.navigateTo
import com.example.cotam.data.UserData
import com.example.cotam.presentation.SharedViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UsersItem(
    userData: UserData,
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    navController: NavController,
    viewModel: SharedViewModel = hiltViewModel(),
    showAllUsers: Boolean,
    showLastMessage: Boolean,
    showNewMessage: Boolean
) {

    var dialogState by remember { mutableStateOf(false) }
    val showListState = remember { mutableStateOf(showAllUsers) }
    val currentUser = viewModel.userData.value
    val messages = viewModel.allMessagesAsync.value
    val messagesSync = viewModel.allMessagesSync.value

    var totalMessage by remember { mutableStateOf(0) }
    totalMessage = messages.count { it.senderUserId == userData.userId }

    var totalMessageSync by remember { mutableStateOf(0) }
    totalMessageSync = messagesSync.count { it.senderUserId == userData.userId }



    if (userData.userId == auth.currentUser?.uid) {
        return
    } else if (currentUser?.gotMsgFrom?.contains(userData.userId) == true) {
        showListState.value = true
    } else if (currentUser?.sendMsgTo?.contains(userData.userId) == true) {
        showListState.value = true
    }

    if (showListState.value) {
        viewModel.getMessagesAsync()


        Row(modifier = Modifier
            .clickable {
                navigateTo(navController, "message", NavParam("userData", userData))
                viewModel.getMessagesSync()
            }
            .fillMaxWidth()
            .padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
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

                Row {
                    Text(text = userData.username ?: "", fontSize = 18.sp)
                    if (showNewMessage) {
                        Spacer(modifier = Modifier.size(10.dp))
                        if (totalMessage > totalMessageSync) {
                            Text(
                                text = (totalMessage - totalMessageSync).toString(),
                                color = Color.White,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(colorResource(id = R.color.green))
                                    .padding(start = 5.dp, end = 5.dp)
                            )
                        }
                    }

                }

                if (showLastMessage) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var lastMsg = ""
                        var lastImg = ""
                        var lastMsgTime = ""

                        for (i in messages) {
                            if (i.getterUserId == userData.userId && i.senderUserId == auth.currentUser?.uid ||
                                i.senderUserId == userData.userId && i.getterUserId == auth.currentUser?.uid
                            ) {
                                lastMsg = i.message.toString()
                                lastImg = i.imageUrl.toString()
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

                        Text(text = lastMsgTime, color = Color.DarkGray, fontSize = 14.sp)
                    }

                }


            }


        }
    }
}