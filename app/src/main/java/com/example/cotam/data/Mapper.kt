package com.example.cotam.data

import com.example.cotam.data.local.UserEntity
import com.example.cotam.data.remote.UserData

fun UserData.toUserEntity() = UserEntity(
    username = username, userImage = image, userId = userId
)

fun UserEntity.toUserData() = UserData(
    userId, username, userImage
)