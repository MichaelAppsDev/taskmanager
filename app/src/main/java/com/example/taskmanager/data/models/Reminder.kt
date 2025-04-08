package com.example.taskmanager.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val userId: String,
    val title: String,
    val description: String,
    val date: Date,
    val isRead: Boolean = false
) 