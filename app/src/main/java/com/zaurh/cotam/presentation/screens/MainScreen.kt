package com.zaurh.cotam.presentation.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import com.zaurh.cotam.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.messaging.FirebaseMessaging
import com.zaurh.cotam.presentation.components.RoomUsersItem
import com.zaurh.cotam.presentation.screens.components.SearchBar
import com.zaurh.cotam.presentation.screens.viewmodel.AuthViewModel
import com.zaurh.cotam.presentation.screens.viewmodel.RoomViewModel
import com.zaurh.cotam.presentation.screens.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "PermissionLaunchedDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    roomViewModel: RoomViewModel = hiltViewModel(),
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val currentUserId = authViewModel.currentUserId
    var searchQuery by remember { mutableStateOf("") }
    val roomUsers = roomViewModel.searchUser(searchQuery).collectAsState(listOf())

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
        println("UserID: $currentUserId")
        val user = userViewModel.userData.value
        currentUserId?.let {
            userViewModel.getUserData(it)
        }
        user?.let { userData ->
            registrationToken.addOnSuccessListener {
                userViewModel.updateUser(
                    userData = user.copy(
                        token = it
                    )
                )
            }
        }
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(30.dp),
                onClick = { navController.navigate("people") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "", tint = Color.White)
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (selectedUsers.isNotEmpty()) "${selectedUsers.size}" else "Co Tam?")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    MaterialTheme.colorScheme.background
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

                                }, text = {
                                    Text(text = "Delete")
                                })
                                DropdownMenuItem(onClick = {
                                    selectedUsers.clear()
                                    selectedUsers.addAll(roomUsers.value)
                                    dropdownState = false
                                }, text = {
                                    Text(text = "Select all")
                                })
                            }
                        }
                    } else {
                        IconButton(onClick = {
                            searchState = !searchState
                        }) {
                            Icon(
                                painter = painterResource(id = if (searchState) R.drawable.search_off else R.drawable.search),
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
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(it),
                ) {
                    if (searchState) {
                        LaunchedEffect(key1 = true) {
                            focusRequester.requestFocus()
                        }
                        SearchBar(
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .padding(start = 10.dp, end = 10.dp, top = 10.dp),
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
            }
        }
    )

    if (searchState) {
        BackHandler(onBack = {
            searchQuery = ""
            searchState = false
        })
    }

    if (selectedUsers.isNotEmpty()){
        BackHandler(onBack = {
            selectedUsers.clear()
        })
    }


}

