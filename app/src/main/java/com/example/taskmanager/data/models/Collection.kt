package com.example.taskmanager.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "collections")
data class Collection(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val userId: String,
    val imageUrl: String? = null,
    val color: Int? = null,
    val isFavorite: Boolean = false
) 