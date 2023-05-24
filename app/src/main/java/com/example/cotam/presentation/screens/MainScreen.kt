package com.example.cotam.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cotam.R
import com.example.cotam.presentation.SharedViewModel
import com.example.cotam.presentation.components.UsersItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: SharedViewModel = hiltViewModel()
) {

    val swipeRefreshState = rememberSwipeRefreshState(false)
    val users = viewModel.usersData.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Co Tam?")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    Color.Transparent
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate("people")
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "")
                    }
                    IconButton(onClick = {
                        navController.navigate("settings")
                    }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "")
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

                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .weight(8f)
                    ) {

                        items(users) {
                            UsersItem(
                                userData = it,
                                navController = navController,
                                showAllUsers = false,
                                showLastMessage = true,
                                showNewMessage = true
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(2f)
                    ) {
                        FloatingActionButton(
                            containerColor = colorResource(id = R.color.green),
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
        }
    )


}

