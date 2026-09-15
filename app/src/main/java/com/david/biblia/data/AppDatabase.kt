package com.david.biblia.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao interface UserDao {
 @Query("SELECT * FROM favorites ORDER BY createdAt DESC") fun favorites():Flow<List<FavoriteEntity>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun addFavorite(v:FavoriteEntity)
 @Delete suspend fun removeFavorite(v:FavoriteEntity)
 @Query("SELECT COUNT(*) FROM favorites WHERE versionId=:version AND bookId=:book AND chapter=:chapter AND verse=:verse") suspend fun favoriteCount(version:String,book:String,chapter:Int,verse:Int):Int
 @Query("SELECT * FROM notes ORDER BY updatedAt DESC") fun notes():Flow<List<NoteEntity>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun saveNote(v:NoteEntity)
 @Delete suspend fun deleteNote(v:NoteEntity)
 @Query("SELECT * FROM highlights") fun highlights():Flow<List<HighlightEntity>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun saveHighlight(v:HighlightEntity)
 @Delete suspend fun deleteHighlight(v:HighlightEntity)
 @Query("SELECT * FROM reading_progress WHERE planId=:plan") fun progress(plan:String):Flow<List<ReadingProgressEntity>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun setProgress(v:ReadingProgressEntity)
 @Insert suspend fun addHistory(v:HistoryEntity)
 @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT 30") fun history():Flow<List<HistoryEntity>>
}
@Database(entities=[FavoriteEntity::class,NoteEntity::class,HighlightEntity::class,ReadingProgressEntity::class,HistoryEntity::class], version=1, exportSchema=true)
abstract class AppDatabase:RoomDatabase(){ abstract fun userDao():UserDao }
