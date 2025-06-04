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
    val userId: String,
    val isCompleted: Boolean = false,
    val category: String? = null,
    val priority: Priority = Priority.NORMAL,
    val status: TaskStatus = TaskStatus.UNCOMPLETED,
    val createdAt: Date = Date(),
    val isArchived: Boolean = false,
    val collectionId: String? = null,
    val originalCollectionId: String? = null
)

enum class Priority {
    LOW, NORMAL, HIGH
}

enum class TaskStatus {
    UNCOMPLETED, COMPLETED, OUTDATED
} 