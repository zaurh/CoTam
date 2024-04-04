@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.cotam.presentation.screens.auth

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.zaurh.cotam.R
import com.zaurh.cotam.common.MyCheckSignedIn
import com.zaurh.cotam.common.MyProgressBar
import com.zaurh.cotam.presentation.screens.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    MyCheckSignedIn(navController = navController, viewModel = authViewModel)

    val context = LocalContext.current
    val isAuthLoading = authViewModel.isAuthLoading.value
    val focus = LocalFocusManager.current

    var usernameTf by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }

    var emailTf by remember { mutableStateOf("") }
    var emailTfError by remember { mutableStateOf(false) }

    var passwordTf by remember { mutableStateOf("") }
    var passwordTfError by remember { mutableStateOf(false) }

    var confirmPasswordTf by remember { mutableStateOf("") }
    var confirmPasswordTfError by remember { mutableStateOf(false) }

    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    val notificationPermissionState = rememberPermissionState(
        permission = Manifest.permission.POST_NOTIFICATIONS
    )


    ///token
    var token by remember { mutableStateOf("") }
    val registrationToken = FirebaseMessaging.getInstance().token

    registrationToken.addOnSuccessListener {
        token = it
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.White
            )

    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(30.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign up",
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.size(40.dp))
            TextField(
                keyboardActions = KeyboardActions(
                    onDone = {
                        focus.clearFocus()
                    }
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        color = if (usernameError) Color.Red else Color.DarkGray,
                        CircleShape
                    ),
                value = usernameTf,
                onValueChange = { usernameTf = it.lowercase() },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "",
                        tint = Color.DarkGray
                    )
                },
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Username",
                        color = Color.Gray,
                        modifier = Modifier.alpha(0.5f)
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    if (usernameError)
                        Icon(
                            painterResource(id = R.drawable.error),
                            "error",
                            tint = MaterialTheme.colorScheme.error
                        )
                }
            )
            if (usernameError) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Please enter username.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            TextField(
                keyboardActions = KeyboardActions(
                    onDone = {
                        focus.clearFocus()
                    }
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        color = if (emailTfError) Color.Red else Color.DarkGray,
                        CircleShape
                    ),
                value = emailTf,
                onValueChange = { emailTf = it },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "",
                        tint = Color.DarkGray
                    )
                },
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Email",
                        color = Color.Gray,
                        modifier = Modifier.alpha(0.5f)
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    if (emailTfError)
                        Icon(
                            painterResource(id = R.drawable.error),
                            "error",
                            tint = MaterialTheme.colorScheme.error
                        )
                }
            )
            if (emailTfError) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Please enter email.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            TextField(
                keyboardActions = KeyboardActions(
                    onDone = {
                        focus.clearFocus()
                    }
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        color = if (passwordTfError) Color.Red else Color.DarkGray,
                        CircleShape
                    ),
                value = passwordTf,
                onValueChange = { passwordTf = it },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                singleLine = true,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "",
                        tint = Color.DarkGray
                    )
                },
                placeholder = {
                    Text(
                        text = "Password",
                        color = Color.Gray,
                        modifier = Modifier.alpha(0.5f)
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (passwordTfError)
                            Icon(
                                painterResource(id = R.drawable.error),
                                "error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        IconButton(onClick = {
                            passwordVisibility = !passwordVisibility
                        }) {
                            Icon(
                                painter =
                                if (passwordVisibility)
                                    painterResource(id = R.drawable.visibility)
                                else
                                    painterResource(id = R.drawable.visibility_off),
                                contentDescription = "",
                                tint = Color.DarkGray
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            if (passwordTfError) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Please enter password.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            TextField(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        color = if (confirmPasswordTfError) Color.Red else Color.DarkGray,
                        CircleShape
                    ),
                value = confirmPasswordTf,
                onValueChange = { confirmPasswordTf = it },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                keyboardActions = KeyboardActions(onDone = {
                    usernameError = usernameTf.isEmpty()
                    emailTfError = emailTf.isEmpty()
                    passwordTfError = passwordTf.isEmpty()
                    confirmPasswordTfError = confirmPasswordTf.isEmpty()
                    if (!usernameError && !emailTfError && !passwordTfError && !confirmPasswordTfError) {
                        authViewModel.signUp(
                            emailTf,
                            passwordTf,
                            confirmPasswordTf,
                            context,
                            usernameTf,
                            token
                        )
                    }
                    focus.clearFocus()
                    notificationPermissionState.launchPermissionRequest()
                    Firebase.messaging.subscribeToTopic("cotam")
                }),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "",
                        tint = Color.DarkGray
                    )
                },
                placeholder = {
                    Text(
                        text = "Confirm password",
                        color = Color.Gray,
                        modifier = Modifier.alpha(0.5f)
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (confirmPasswordTfError)
                            Icon(
                                painterResource(id = R.drawable.visibility),
                                "error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        IconButton(onClick = {
                            confirmPasswordVisibility = !confirmPasswordVisibility
                        }) {
                            Icon(
                                painter =
                                if (confirmPasswordVisibility)
                                    painterResource(id = R.drawable.visibility)
                                else
                                    painterResource(id = R.drawable.visibility_off),
                                contentDescription = "",
                                tint = Color.DarkGray
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            if (confirmPasswordTfError) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Please enter password again.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(30.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.blue)
                ),
                onClick = {
                    usernameError = usernameTf.isEmpty()
                    emailTfError = emailTf.isEmpty()
                    passwordTfError = passwordTf.isEmpty()
                    confirmPasswordTfError = confirmPasswordTf.isEmpty()
                    if (!usernameError && !emailTfError && !passwordTfError && !confirmPasswordTfError) {
                        authViewModel.signUp(
                            emailTf,
                            passwordTf,
                            confirmPasswordTf,
                            context,
                            usernameTf.trim().replace(" ", ""),
                            token
                        )
                    }
                    focus.clearFocus()
                    notificationPermissionState.launchPermissionRequest()
                    Firebase.messaging.subscribeToTopic("cotam")
                }) {
                Text(
                    text = "Sign up",
                    color = Color.White,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }


        }

        if (isAuthLoading) {
            MyProgressBar()
        }

    }

}