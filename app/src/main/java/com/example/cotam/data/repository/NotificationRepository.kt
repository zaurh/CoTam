package com.example.cotam.data.repository

import com.example.cotam.common.notification.NotificationApi
import com.example.cotam.data.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val api: NotificationApi
) {
    suspend fun sendNotification(notification: PushNotification): Response<ResponseBody> {
        return api.postNotification(notification)
    }
}