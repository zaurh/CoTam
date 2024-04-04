package com.zaurh.cotam.data.repository

import com.zaurh.cotam.common.notification.NotificationApi
import com.zaurh.cotam.data.remote.PushNotification
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