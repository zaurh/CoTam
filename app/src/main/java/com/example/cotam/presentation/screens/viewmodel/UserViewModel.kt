package com.example.cotam.presentation.screens.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cotam.data.UserData
import com.example.cotam.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo
): ViewModel() {

    val isUserLoading = userRepo.isUserLoading
    val userData = userRepo.userData
    val usersData = userRepo.usersData
    val currentUserId = userRepo.currentUserId

    private var isSearchStarting = true
    private var initialUsers = listOf<UserData>()



    fun addUser(userData: UserData){
        userRepo.addUser(userData)
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