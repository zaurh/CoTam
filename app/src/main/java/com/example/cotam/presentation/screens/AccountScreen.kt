package com.example.cotam.presentation.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.cotam.R
import com.example.cotam.common.MyProgressBar
import com.example.cotam.common.ZoomableImg
import com.example.cotam.data.remote.UserData
import com.example.cotam.presentation.screens.viewmodel.AuthViewModel
import com.example.cotam.presentation.screens.viewmodel.StorageViewModel
import com.example.cotam.presentation.screens.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountScreen(
    navController: NavController,
    userData: UserData,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    storageViewModel: StorageViewModel
) {

    var imageDialogState by remember { mutableStateOf(false) }
    val isMediaLoading = storageViewModel.isMediaLoading.value
    val focus = LocalFocusManager.current
    var userImage by remember {
        mutableStateOf(
            userData.image ?: "https://shorturl.at/jmoHM"
        )
    }

    var usernameTfError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            }, title = { Text(text = "Account", fontWeight = FontWeight.Bold) })
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(50.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    var usernameTf by remember { mutableStateOf(userData.username ?: "") }

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        uri?.let {
                            storageViewModel.uploadMedia(it, "images") {
                                userImage = it.toString()
                            }
                        }
                    }

                    Box() {
                        Image(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .clickable {
                                    imageDialogState = true
                                },
                            painter = rememberImagePainter(
                                data = userImage
                            ),
                            contentDescription = ""
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "",
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    launcher.launch("image/*")
                                }
                                .background(colorResource(id = R.color.blue))
                                .padding(5.dp)
                                .size(30.dp)
                                .align(
                                    Alignment.BottomEnd
                                ),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.size(20.dp))

                    TextField(
                        label = { Text(text = "Username") },
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focus.clearFocus()
                            }
                        ),
                        trailingIcon = {
                            if (usernameTfError)
                                Icon(
                                    Icons.Filled.Warning,
                                    "error",
                                    tint = colorResource(id = R.color.red)
                                )
                        },
                        singleLine = true,
                        maxLines = 1,
                        colors = TextFieldDefaults.textFieldColors(
                            unfocusedIndicatorColor = if (usernameTfError) colorResource(id = R.color.red) else Color.Gray,
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = colorResource(id = R.color.blue),
                        ),
                        value = usernameTf,
                        onValueChange = { usernameTf = it },

                        )

                    Spacer(modifier = Modifier.size(30.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.blue)
                        ),
                        onClick = {
                            focus.clearFocus()
                            usernameTfError = usernameTf.isEmpty()
                            if (usernameTf.isNotEmpty()) {
                                userViewModel.updateUser(
                                    userData = userData.copy(
                                        username = usernameTf.trim().replace(" ", ""),
                                        image = userImage
                                    )
                                )
                                navController.popBackStack()
                            }

                        }) {
                        Text(text = "Save", color = Color.White)
                    }
                }
            }
            if (isMediaLoading) {
                MyProgressBar()
            }
        },
        bottomBar = {

        }
    )

    if (imageDialogState) {
            Dialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = {
                    imageDialogState = false
                }) {
                ZoomableImg(url = userImage)
            }
    }


}