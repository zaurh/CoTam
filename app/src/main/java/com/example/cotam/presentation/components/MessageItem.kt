package com.example.cotam.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.example.cotam.R
import com.example.cotam.common.ZoomableImg
import com.example.cotam.common.myTime
import com.example.cotam.data.MessageData
import com.example.cotam.presentation.SharedViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageItem(
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    messageData: MessageData,
    longClick: () -> Unit,
    viewModel: SharedViewModel = hiltViewModel()
) {

    var deleteState by remember { mutableStateOf(false) }
    var dialogState by remember { mutableStateOf(false) }

    val senderUid = messageData.senderUserId
    val currentUid = auth.currentUser?.uid

    var senderSide by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        if (senderUid == currentUid) {
            senderSide = true
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        longClick()
                        if (messageData.senderUserId == auth.currentUser?.uid) {
                            deleteState = !deleteState
                        }
                    }
                )
                .padding(10.dp),
            horizontalAlignment = if (senderUid == currentUid) Alignment.End else Alignment.Start
        ) {
            if (dialogState) {

                Surface(modifier = Modifier.fillMaxSize()) {
                    Dialog(
                    properties = DialogProperties(usePlatformDefaultWidth = false),

                    onDismissRequest = {
                            dialogState = false
                        }) {
                        ZoomableImg(url = messageData.imageUrl ?: "")
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
                                dialogState = true
                            },
                            onLongClick = {
                                longClick()
                                if (messageData.senderUserId == auth.currentUser?.uid) {
                                    deleteState = !deleteState
                                }
                            }
                        ),
                    painter = rememberImagePainter(data = messageData.imageUrl),
                    contentDescription = ""
                )
            }
            if (messageData.message?.isNotEmpty() == true) {
                Column(
                    modifier = Modifier
                        .clip(CircleShape)
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
            Text(text = myTime(messageData.time!!), fontSize = 12.sp)
            if (deleteState) {
                Column(horizontalAlignment = Alignment.End) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.red)
                        ),
                        onClick = {
                            viewModel.deleteMessage(messageData.messageId ?: "")
                            deleteState = false
                        }) {
                        Text(text = "Delete for everyone", fontSize = 12.sp)
                    }
                    Button(colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    ), onClick = { deleteState = false }) {
                        Text(text = "Cancel", fontSize = 12.sp)
                    }
                }
            }


        }


    }
}