package com.example.cotam.presentation.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cotam.R
import com.example.cotam.data.remote.UserData
import com.example.cotam.presentation.components.RoomUsersItem
import com.example.cotam.presentation.screens.components.SearchBar
import com.example.cotam.presentation.screens.viewmodel.RoomViewModel
import com.example.cotam.presentation.screens.viewmodel.UserViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.messaging.FirebaseMessaging

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "PermissionLaunchedDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    roomViewModel: RoomViewModel = hiltViewModel(),
    userViewModel: UserViewModel
) {

    val roomUsers = roomViewModel.userData.observeAsState(listOf())
    val selectedUsers = roomViewModel.selectedUsers


    var dropdownState by remember { mutableStateOf(false) }
    var settingsDropdownState by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(false)
    val token = remember { mutableStateOf("a") }
    var searchState by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }


    val registrationToken = FirebaseMessaging.getInstance().token


    LaunchedEffect(true) {
        val user = userViewModel.userData.value

        user?.let {
            registrationToken.addOnSuccessListener {
                userViewModel.updateUser(
                    userData = user.copy(
                        token = it
                    )
                )
            }
        }
    }

    if (searchState) {
        BackHandler(onBack = {
            roomViewModel.getUsers()
            searchState = false
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (selectedUsers.isNotEmpty()) "${selectedUsers.size}" else "Co Tam?")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    colorResource(id = R.color.blue)
                ),
                actions = {
                    if (selectedUsers.isNotEmpty()) {
                        IconButton(onClick = {
                            dropdownState = true
                        }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                        }
                        if (dropdownState) {
                            DropdownMenu(
                                expanded = dropdownState,
                                onDismissRequest = { dropdownState = false })
                            {
                                DropdownMenuItem(onClick = {
                                    for (user in selectedUsers) {
                                        roomViewModel.deleteUserById(
                                            userId = user.userId!!
                                        )
                                    }
                                    selectedUsers.clear()

                                }) {
                                    Text(text = "Delete")
                                }
                                DropdownMenuItem(onClick = {
                                    selectedUsers.clear()
                                    selectedUsers.addAll(roomUsers.value)
                                    dropdownState = false
                                }) {
                                    Text(text = "Select all")
                                }
                            }
                        }
                    } else {
                        IconButton(onClick = {
                            searchState = !searchState
                        }) {
                            Icon(
                                imageVector = if (searchState) Icons.Default.SearchOff else Icons.Default.Search,
                                contentDescription = ""
                            )
                        }
                        IconButton(onClick = {
                            navController.navigate("settings")
                        }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                        }
                    }

                },
                navigationIcon = {
                    if (selectedUsers.isNotEmpty()) {
                        IconButton(onClick = {
                            selectedUsers.clear()
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        content = {
            SwipeRefresh(state = swipeRefreshState, onRefresh = {
                navController.navigate("main") {
                    popUpTo(0)
                }
            }) {
                Column(
                    modifier = Modifier
                        .padding(top = 70.dp),
                ) {

                    if (searchState) {
                        var searchQuery by remember { mutableStateOf("") }
                        LaunchedEffect(key1 = true) {
                            focusRequester.requestFocus()
                        }
                        SearchBar(
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .padding(start = 10.dp, end = 10.dp),
                            textTf = searchQuery,
                            onTextChange = {
                                searchQuery = it
                                roomViewModel.searchUser(it)
                            }
                        ) {
                            focusManager.clearFocus()
                        }
                    }

                    LazyColumn {
                        items(roomUsers.value.reversed()) {
                            RoomUsersItem(
                                userEntity = it,
                                navController = navController,
                                showAllUsers = true,
                                showLastMessage = true
                            )
                        }
                    }

                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    FloatingActionButton(
                        containerColor = colorResource(id = R.color.blue),
                        contentColor = Color.White,
                        modifier = Modifier
                            .padding(30.dp)
                            .align(Alignment.BottomEnd),
                        onClick = { navController.navigate("people") }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")
                    }
                }

            }
        }
    )


}

