package com.example.cotam.common.notification

import com.example.cotam.common.Constants.CONTENT_TYPE
import com.example.cotam.common.Constants.SERVER_KEY
import com.example.cotam.data.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}