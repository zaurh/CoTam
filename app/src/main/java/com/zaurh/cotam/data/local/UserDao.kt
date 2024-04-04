package com.zaurh.cotam.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(userEntity: UserEntity)

    @Delete
    suspend fun delete(userEntity: UserEntity)

    @Query("DELETE FROM user WHERE userId = :userId")
    suspend fun deleteUserByUserId(userId: String)

    @Query("SELECT * FROM user")
    fun allUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE username like '%' || :query || '%'")
    fun searchUser(query: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE userId like '%' || :query || '%'")
    suspend fun searchById(query: String): List<UserEntity>
}