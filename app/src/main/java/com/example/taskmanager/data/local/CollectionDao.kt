package com.example.taskmanager.data.local

import androidx.room.*
import com.example.taskmanager.data.models.Collection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections WHERE userId = :userId")
    fun getAllCollections(userId: String): Flow<List<Collection>>

    @Query("SELECT * FROM collections WHERE id = :collectionId")
    fun getCollectionByIdFlow(collectionId: String): Flow<Collection?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: Collection)

    @Update
    suspend fun update(collection: Collection)

    @Query("DELETE FROM collections WHERE id = :collectionId")
    suspend fun deleteById(collectionId: String)

    @Query("SELECT * FROM collections WHERE userId = :userId AND isFavorite = 1")
    fun getFavoriteCollections(userId: String): Flow<List<Collection>>
} 