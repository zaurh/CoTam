package com.example.cotam.common

object Constants {

    const val BASE_URL = "https://fcm.googleapis.com/"
    const val SERVER_KEY = "AAAABBpZkpw:APA91bFujtBdf09_N0EEEADsoXVtSZN8Xm2sdm4wDOIxzBrnSxh1bQw1kCqCnFGPyNCjqoxludS5Uzxb5Ei8AsVczR9v8seSxwyHh7TmhgkrwCm047ISdR6t6-UOVDXFvlCe-kcBg46U"
    const val CONTENT_TYPE = "application/json"

    const val TOPIC = "/topics/myTopic"

    //if user in another user's chat who sends you message then you don't get notification
    var messagingUsernameNotification = ""
}