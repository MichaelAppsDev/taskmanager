package com.example.taskmanager.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val dueDate: Date? = null,
    val collectionId: String,
    val userId: String,
    val isCompleted: Boolean = false,
    val category: String? = null,
    val priority: Priority = Priority.NORMAL,
    val createdAt: Date = Date()
)

enum class Priority {
    LOW, NORMAL, HIGH
} 