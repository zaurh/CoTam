package com.example.cotam.presentation.screens.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cotam.data.local.UserEntity
import com.example.cotam.data.remote.MessageData
import com.example.cotam.data.repository.RoomRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepo: RoomRepo
) : ViewModel() {

    val userData = roomRepo.userData
    val selectedUsers = mutableStateListOf<UserEntity>()


    init {
        getUsers()
    }

    fun getUsers() {
        viewModelScope.launch {
            roomRepo.getUsers()
        }
    }

    fun addUser(userEntity: UserEntity) {
        viewModelScope.launch {
            roomRepo.addUser(userEntity)
        }
        getUsers()
    }

    fun deleteUserById(userId: String) {
        viewModelScope.launch {
            roomRepo.deleteUserById(userId)
            getUsers()
        }
    }

    fun searchUser(query: String) {
        viewModelScope.launch {
            roomRepo.searchUser(query)
        }
    }

    fun clearSearch() {
        getUsers()
    }

}