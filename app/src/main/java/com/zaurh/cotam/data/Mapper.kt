package com.zaurh.cotam.data

import com.zaurh.cotam.data.local.UserEntity
import com.zaurh.cotam.data.remote.UserData

fun UserData.toUserEntity() = UserEntity(
    username = username, userImage = image, userId = userId, token = token
)

fun UserEntity.toUserData() = UserData(
    userId, username, userImage, token
)