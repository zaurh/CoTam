package com.example.cotam.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.cotam.data.local.UserDao
import com.example.cotam.data.local.UserEntity
import javax.inject.Inject

class RoomRepo @Inject constructor(
    private val dao: UserDao
) {
    var userData = MutableLiveData<List<UserEntity>>()

    init {
        userData = MutableLiveData()
    }

    suspend fun getUsers() {
        userData.value = dao.allUsers()
    }

    suspend fun addUser(userEntity: UserEntity) {
        dao.insert(userEntity)
    }

    suspend fun deleteUser(userId: String) {
        val userDeletion = UserEntity(
            userId = userId
        )
        dao.delete(userDeletion)
    }

    suspend fun deleteUserById(userId: String) {
        dao.deleteUserByUserId(userId)
    }

    suspend fun searchUser(query: String) {
        userData.value = dao.searchUser(query)
    }
}