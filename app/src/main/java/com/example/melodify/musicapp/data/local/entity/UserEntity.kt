package com.example.melodify.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val username: String,
    val profileImage: String,
    val followersCount: Int,
    val followingCount: Int,
    val isPremium: Boolean
)