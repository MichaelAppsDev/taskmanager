package com.example.taskmanager.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val uid: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?
) 