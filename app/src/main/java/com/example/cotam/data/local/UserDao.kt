package com.example.cotam.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(userEntity: UserEntity)

    @Delete
    suspend fun delete(userEntity: UserEntity)

    @Query("DELETE FROM user WHERE userId = :userId")
    suspend fun deleteUserByUserId(userId: String)

    @Query("SELECT * FROM user")
    suspend fun allUsers(): List<UserEntity>

    @Query("SELECT * FROM user WHERE username like '%' || :query || '%'")
    suspend fun searchUser(query: String): List<UserEntity>
}