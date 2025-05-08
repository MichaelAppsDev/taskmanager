package com.example.taskmanager.data.local

import androidx.room.*
import com.example.taskmanager.data.models.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId")
    fun getAllRemindersFlow(userId: String): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE taskId = :taskId")
    fun getRemindersByTaskIdFlow(taskId: String): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: String): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteById(reminderId: String)

    @Query("DELETE FROM reminders WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: String)

    @Query("SELECT COUNT(*) FROM reminders WHERE userId = :userId AND isRead = 0")
    fun getUnreadRemindersCount(userId: String): Flow<Int>
} 