package com.example.cotam.presentation.screens.auth

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cotam.R
import com.example.cotam.common.MyCheckSignedIn
import com.example.cotam.common.MyProgressBar
import com.example.cotam.presentation.screens.viewmodel.AuthViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SignInScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    MyCheckSignedIn(navController = navController, viewModel = authViewModel)

    val isAuthLoading = authViewModel.isAuthLoading.value
    val focus = LocalFocusManager.current
    var emailTf by remember { mutableStateOf("zaur@gmail.com") }
    var emailTfError by remember { mutableStateOf(false) }
    var passwordTf by remember { mutableStateOf("zaur1234") }
    var passwordTfError by remember { mutableStateOf(false) }

    var passwordVisibility by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                Color.White
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
                text = "Welcome!",
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
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
                    backgroundColor = Color.White,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    if (emailTfError)
                        Icon(Icons.Filled.Warning, "error", tint = MaterialTheme.colors.error)
                }
            )
            if (emailTfError) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Please enter email.",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
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
                        color = if (passwordTfError) Color.Red else Color.DarkGray,
                        CircleShape
                    ),
                value = passwordTf,
                onValueChange = { passwordTf = it },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                keyboardActions = KeyboardActions(onDone = {
                    emailTfError = emailTf.isEmpty()
                    passwordTfError = passwordTf.isEmpty()

                    if (!emailTfError && !passwordTfError) {
                        authViewModel.signIn(
                            email = emailTf,
                            password = passwordTf,
                            context = context
                        )
                    }
                    focus.clearFocus()
                    notificationPermissionState.launchPermissionRequest()
                    Firebase.messaging.subscribeToTopic("cotam")
                }),
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
                            Icon(Icons.Filled.Warning, "error", tint = MaterialTheme.colors.error)
                        IconButton(onClick = {
                            passwordVisibility = !passwordVisibility
                        }) {
                            Icon(
                                imageVector =
                                if (passwordVisibility)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = "",
                                tint = Color.DarkGray
                            )
                        }
                    }

                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            if (passwordTfError) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Please enter password.",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "Forgot password?",
                    color = colorResource(id = R.color.green),
                    modifier = Modifier.clickable {
                        navController.navigate("forgot_password")
                    })
            }
            Spacer(modifier = Modifier.size(30.dp))
            androidx.compose.material.Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.green)
                ),
                onClick = {
                    emailTfError = emailTf.isEmpty()
                    passwordTfError = passwordTf.isEmpty()

                    if (!emailTfError && !passwordTfError) {
                        authViewModel.signIn(
                            email = emailTf,
                            password = passwordTf,
                            context = context
                        )
                    }
                    focus.clearFocus()

                    notificationPermissionState.launchPermissionRequest()
                    Firebase.messaging.subscribeToTopic("cotam")
                }) {
                Text(
                    text = "Sign in",
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