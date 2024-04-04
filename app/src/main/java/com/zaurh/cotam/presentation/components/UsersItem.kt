package com.zaurh.cotam.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.zaurh.cotam.common.NavParam
import com.zaurh.cotam.common.ZoomableImg
import com.zaurh.cotam.common.navigateTo
import com.zaurh.cotam.data.remote.UserData

@Composable
fun UsersItem(
    userData: UserData,
    navController: NavController,
    showAllUsers: Boolean,
) {

    var dialogState by remember { mutableStateOf(false) }
    val showListState = remember { mutableStateOf(showAllUsers) }


    if (showListState.value) {
        Row(
            modifier = Modifier
                .clickable {
                    navigateTo(navController, "message", NavParam("userData", userData))
                }
                .fillMaxWidth()
                .padding(10.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            if (dialogState) {
                Dialog(onDismissRequest = {
                    dialogState = false
                }) {
                    Box(modifier = Modifier.size(400.dp), Alignment.Center) {
                        ZoomableImg(url = userData.image ?: "")
                    }
                }
            }
            Image(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (userData.image?.isNotEmpty() == true) {
                            dialogState = true
                        }
                    },
                contentScale = ContentScale.Crop,
                painter = rememberImagePainter(
                    data = userData.image ?: "https://i.hizliresim.com/x7e0wpo.png"
                ),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.size(10.dp))

            Text(text = userData.username ?: "", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)


        }
    }
}