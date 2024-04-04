package com.zaurh.cotam.presentation.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.cotam.data.remote.UserData
import com.zaurh.cotam.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo,
): ViewModel() {

    val userData = userRepo.userData
    val usersData = userRepo.usersData
    private val currentUserId = userRepo.currentUserId


    private var isSearchStarting = true
    private var initialUsers = listOf<UserData>()

    init {
        currentUserId?.let {
            getUserData(it)
        }
    }

    fun getUserData(userId: String){
        userRepo.getUserData(userId)
    }

    fun updateUser(userData: UserData){
        userRepo.updateUser(userData)
    }

    fun searchUser(query: String) {
        val listToSearch = if (isSearchStarting) {
            usersData.value
        } else {
            initialUsers
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                usersData.value = initialUsers
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.username!!.contains(
                    query.trim(),
                    ignoreCase = true
                )
            }
            if (isSearchStarting) {
                initialUsers = usersData.value
                isSearchStarting = false
            }
            usersData.value = results
        }
    }

    fun clearSearch() {
        usersData.value = initialUsers
        isSearchStarting = true
    }
}