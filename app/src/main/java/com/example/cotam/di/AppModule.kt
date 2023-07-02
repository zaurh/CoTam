package com.example.cotam.di

import com.example.cotam.common.Constants.BASE_URL
import com.example.cotam.common.notification.NotificationApi
import com.example.cotam.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //Firebase Auth
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    //Firebase FireStore
    @Singleton
    @Provides
    fun provideFireStore(): FirebaseFirestore = Firebase.firestore

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage


    //Retrofit
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Singleton
    @Provides
    fun provideRetrofitApi(retrofit: Retrofit): NotificationApi{
        return retrofit.create(NotificationApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRepository(api: NotificationApi) = NotificationRepository(api)

}