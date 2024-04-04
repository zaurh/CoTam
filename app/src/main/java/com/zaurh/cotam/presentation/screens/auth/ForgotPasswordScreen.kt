@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.cotam.presentation.screens.auth

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaurh.cotam.R
import com.zaurh.cotam.common.MyProgressBar
import com.zaurh.cotam.presentation.screens.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val isLoading = authViewModel.isAuthLoading.value
    val focus = LocalFocusManager.current
    var emailTf by remember { mutableStateOf("") }
    var emailTfError by remember { mutableStateOf(false) }

    val context = LocalContext.current


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
                text = "Forgot Password",
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.size(40.dp))
            Text(
                text = "Enter the email associated with your account and we'll send an email with instructions to reset your password",
                color = Color.Gray,
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
            )
            Spacer(modifier = Modifier.size(20.dp))
            TextField(
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
                keyboardActions = KeyboardActions(onDone = {
                    if (emailTf.isEmpty()) {
                        emailTfError = true
                    } else {
                        authViewModel.forgotPassword(emailTf, context)
                        focus.clearFocus()
                    }
                }),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    if (emailTfError)
                        Icon(
                            painter = painterResource(id = R.drawable.copy),
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
            Spacer(modifier = Modifier.size(30.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.blue)
                ),
                onClick = {
                    if (emailTf.isEmpty()) {
                        emailTfError = true
                    } else {
                        authViewModel.forgotPassword(emailTf, context)
                        focus.clearFocus()
                    }
                }) {
                Text(
                    text = "Send",
                    color = Color.White,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }

        }

        if (isLoading) {
            MyProgressBar()
        }
    }

}