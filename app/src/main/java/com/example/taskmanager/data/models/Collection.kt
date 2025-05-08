package com.example.taskmanager.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class Collection(
    @PrimaryKey
    val id: String = "",
    val name: String,
    val description: String = "",
    val userId: String,
    val isFavorite: Boolean = false
) 