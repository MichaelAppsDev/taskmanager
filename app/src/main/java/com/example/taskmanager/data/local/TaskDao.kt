package com.example.taskmanager.data.local

import androidx.room.*
import com.example.taskmanager.data.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId")
    suspend fun getTasksByUserId(userId: String): List<Task>

    @Query("SELECT * FROM tasks WHERE collectionId = :collectionId")
    suspend fun getTasksByCollectionId(collectionId: String): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getAllTasks(userId: String): Flow<List<Task>>
} 