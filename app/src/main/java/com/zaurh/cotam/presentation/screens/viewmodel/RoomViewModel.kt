package com.zaurh.cotam.presentation.screens.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.cotam.data.local.UserEntity
import com.zaurh.cotam.data.repository.RoomRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepo: RoomRepo
) : ViewModel() {

    val userData: Flow<List<UserEntity>> = roomRepo.userData

    val selectedUsers = mutableStateListOf<UserEntity>()


    fun deleteUserById(userId: String) {
        viewModelScope.launch {
            roomRepo.deleteUserById(userId)
        }
    }

    fun searchUser(query: String): Flow<List<UserEntity>> {
        return roomRepo.searchUser(query)
    }

}