package com.zaurh.cotam.presentation.screens

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.cotam.R
import com.zaurh.cotam.common.NavParam
import com.zaurh.cotam.common.navigateTo
import com.zaurh.cotam.common.sendMail
import com.zaurh.cotam.data.remote.UserData
import com.zaurh.cotam.presentation.screens.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit
) {

    val uriHandler = LocalUriHandler.current
    val userData = userViewModel.userData.value
    val context = LocalContext.current

    LaunchedEffect(true){
        println("settingsUserId : ${userData?.userId}")
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                },
                title = {
                    Text(
                        text = "${userData?.username}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                })
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surface
                    )
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SettingsItem(
                    text = "Account",
                    leadingIcon = painterResource(id = R.drawable.settings),
                    onClick = {
                        navigateTo(
                            navController,
                            "account",
                            NavParam("userData", userData ?: UserData())
                        )
                    }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "",
                        tint = Color.Gray
                    )
                }
                SettingsItem(
                    text = "Theme",
                    leadingIcon = painterResource(id = R.drawable.dark_mode),
                    onClick = {
                        onThemeUpdated()
                    }) {
                    CustomSwitcher(
                        switch = darkTheme,
                        size = 40.dp,
                        padding = 5.dp,
                        firstIcon = "☀️",
                        secondIcon = "\uD83C\uDF19"
                    )
                }

                SettingsItem(
                    text = "About application",
                    leadingIcon = painterResource(id = R.drawable.about),
                    onClick = {
                        uriHandler.openUri("https://github.com/zaurh/cotam")
                    }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "",
                        tint = Color.Gray
                    )
                }

                SettingsItem(
                    text = "Contact",
                    leadingIcon = painterResource(id = R.drawable.mail),
                    onClick = {
                        context.sendMail(to = "zaurway@gmail.com", subject = "CoTam")
                    }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "",
                        tint = Color.Gray
                    )
                }

            }
        }
    )
}


@Composable
private fun SettingsItem(
    text: String,
    leadingIcon: Painter,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = leadingIcon, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.size(12.dp))
            Text(text = text, color = MaterialTheme.colorScheme.primary)
        }
        content()
    }
}


@Composable
private fun CustomSwitcher(
    switch: Boolean,
    size: Dp = 150.dp,
    firstIcon: String,
    secondIcon: String,
    padding: Dp = 10.dp,
    borderWidth: Dp = 1.dp,
    parentShape: Shape = CircleShape,
    toggleShape: Shape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300),
) {
    val offset by animateDpAsState(
        targetValue = if (!switch) 0.dp else size,
        animationSpec = animationSpec
    )

    Box(
        modifier = Modifier
            .width(size * 2)
            .height(size)
            .clip(shape = parentShape)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = offset)
                .padding(all = padding)
                .clip(shape = toggleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shape = parentShape
                )
        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Text(text = firstIcon)
            }
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Text(text = secondIcon)
            }
        }
    }
}
