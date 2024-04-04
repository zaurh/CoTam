package com.zaurh.cotam.common.notification

import com.zaurh.cotam.common.Constants.CONTENT_TYPE
import com.zaurh.cotam.common.Constants.SERVER_KEY
import com.zaurh.cotam.data.remote.PushNotification
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