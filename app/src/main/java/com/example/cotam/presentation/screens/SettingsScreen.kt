package com.example.cotam.presentation.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.cotam.R
import com.example.cotam.common.MyProgressBar
import com.example.cotam.presentation.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    navController: NavController, viewModel: SharedViewModel = hiltViewModel()
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProfileImage(uri)
        }
    }

    val userData = viewModel.userData.value
    val isImageLoading = viewModel.isImageLoading.value
    val focus = LocalFocusManager.current

    var usernameTfError by remember { mutableStateOf(false) }



    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            userData?.let {
                var usernameTf by remember { mutableStateOf(userData.username ?: "") }

                Box(modifier = Modifier.clickable {
                    launcher.launch("image/*")
                }) {
                    Image(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape),
                        painter = rememberImagePainter(
                            data = userData.image ?: "https://shorturl.at/jmoHM"
                        ),
                        contentDescription = ""
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "",
                        modifier = Modifier
                            .size(30.dp)
                            .align(
                                Alignment.BottomEnd
                            )
                    )
                }

                Spacer(modifier = Modifier.size(20.dp))

                TextField(
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
                    placeholder = { Text(text = "Username") },
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
                        containerColor = colorResource(id = R.color.green)
                    ),
                    onClick = {
                        focus.clearFocus()
                        usernameTfError = usernameTf.isEmpty()
                        if (usernameTf.isNotEmpty()) {
                            viewModel.updateUser(
                                usernameTf.trim().replace(" ", ""), imageUrl = userData.image
                            )
                            navController.navigate("main") {
                                popUpTo(0)
                            }
                        }

                    }) {
                    Text(text = "Save", color = Color.White)
                }
                Spacer(modifier = Modifier.size(80.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.red)
                    ),
                    onClick = {
                        focus.clearFocus()
                        viewModel.signOut()
                        navController.navigate("auth") {
                            popUpTo(0)
                        }

                    }) {
                    Text(text = "Sign Out", color = Color.White)
                }
            }
        }
    }
    if (isImageLoading) {
        MyProgressBar()
    }
}