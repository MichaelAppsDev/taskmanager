package com.example.taskmanager.data.local

import androidx.room.*
import com.example.taskmanager.data.models.Collection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections WHERE userId = :userId")
    suspend fun getCollectionsByUserId(userId: String): List<Collection>

    @Query("SELECT * FROM collections WHERE id = :collectionId")
    suspend fun getCollectionById(collectionId: String): Collection?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: Collection)

    @Update
    suspend fun updateCollection(collection: Collection)

    @Delete
    suspend fun deleteCollection(collection: Collection)

    @Query("SELECT * FROM collections WHERE userId = :userId")
    fun getAllCollections(userId: String): Flow<List<Collection>>
} 