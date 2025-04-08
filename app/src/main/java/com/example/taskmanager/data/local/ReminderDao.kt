package com.example.taskmanager.data.local

import androidx.room.*
import com.example.taskmanager.data.models.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId")
    suspend fun getRemindersByUserId(userId: String): List<Reminder>

    @Query("SELECT * FROM reminders WHERE taskId = :taskId")
    suspend fun getRemindersByTaskId(taskId: String): List<Reminder>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: String): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE userId = :userId")
    fun getAllReminders(userId: String): Flow<List<Reminder>>
} 