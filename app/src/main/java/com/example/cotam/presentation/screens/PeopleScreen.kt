package com.example.cotam.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cotam.presentation.components.UsersItem
import com.example.cotam.presentation.screens.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {

    val users = userViewModel.usersData.value
    val focusManager = LocalFocusManager.current
    var text by rememberSaveable { mutableStateOf("") }
    val focus = FocusRequester()

    LaunchedEffect(key1 = true) {
        focus.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Box(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

                BackHandler(enabled = text.isNotEmpty(), onBack = {
                    text = ""
                    userViewModel.clearSearch()
                    focusManager.clearFocus()
                })
                TextField(
                    placeholder = { Text(text = "Search...") },
                    colors = TextFieldDefaults.textFieldColors(
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    value = text,
                    onValueChange = {
                        text = it
                        userViewModel.searchUser(it)
                    },
                    maxLines = 1,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black, fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .focusRequester(focus)
                        .clip(CircleShape)
                        .fillMaxWidth()

                )
            }

            Spacer(modifier = Modifier.size(20.dp))
            Text(text = "Recommended", color = Color.Gray)
            LazyColumn {
                items(users) {
                    UsersItem(
                        userData = it,
                        navController = navController,
                        showAllUsers = true,
                        showLastMessage = false
                    )
                }
            }

        }
    }

}