package com.example.cotam.presentation.screens

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.cotam.R
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

    val isImageLoading = viewModel.isImageLoading.value
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val messages = viewModel.messageData.value
    var message by remember { mutableStateOf("") }
    var allMessages by remember { mutableStateOf(false) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.sendImage(
                uri = it,
                message = message,
                getterUserId = userData.userId ?: "",
                getterUsername = userData.username ?: "",
                getterUserImage = userData.image ?: ""
            )
        }
    }




    SideEffect {
        allMessages = true
        scope.launch {
            scrollState.scrollToItem(messages.size)
        }
    }

    if (allMessages) {
        viewModel.getPrivateMessages(userData.userId ?: "")
    }



    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                },
                title = {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(35.dp),
                            painter = rememberImagePainter(
                                data = userData.image ?: "https://shorturl.at/jmoHM"
                            ),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = "${userData.username}")

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
                        MessageItem(messageData = it, longClick = {

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

                TextField(
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(CircleShape),
                    colors = TextFieldDefaults.textFieldColors(
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    value = message,
                    onValueChange = { message = it },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = {
                                launcher.launch("image/*")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = ""
                                )
                            }
                            IconButton(onClick = {
                                if (message.trim().isNotEmpty()) {
                                    scope.launch {
                                        if (messages.isNotEmpty()) {
                                            scrollState.scrollToItem(messages.size)
                                        }
                                    }
                                    viewModel.sendMessage(
                                        message = message.trimStart().trimEnd(),
                                        getterUsername = userData.username ?: "",
                                        getterUserImage = userData.image ?: "",
                                        getterUserId = userData.userId ?: ""
                                    )
                                    message = ""
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