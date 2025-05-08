package com.example.taskmanager.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.taskmanager.data.local.converters.DateTimeConverters
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "tasks")
@TypeConverters(DateTimeConverters::class)
data class Task(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val deadline: LocalDateTime? = null,
    val collectionId: String,
    val userId: String,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
) 