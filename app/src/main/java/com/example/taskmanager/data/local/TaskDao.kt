package com.example.taskmanager.data.local

import androidx.room.*
import com.example.taskmanager.data.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getAllTasksFlow(userId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE collectionId = :collectionId")
    fun getTasksByCollectionIdFlow(collectionId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: String)

    @Query("DELETE FROM tasks WHERE collectionId = :collectionId")
    suspend fun deleteByCollectionId(collectionId: String)
} 