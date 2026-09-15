package com.david.biblia.data
import androidx.room.*

@Entity(tableName="favorites", primaryKeys=["versionId","bookId","chapter","verse"])
data class FavoriteEntity(val versionId:String,val bookId:String,val chapter:Int,val verse:Int,val createdAt:Long=System.currentTimeMillis())
@Entity(tableName="notes", indices=[Index(value=["versionId","bookId","chapter","verse"], unique=true)])
data class NoteEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val versionId:String,val bookId:String,val chapter:Int,val verse:Int,val text:String,val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="highlights", primaryKeys=["versionId","bookId","chapter","verse"])
data class HighlightEntity(val versionId:String,val bookId:String,val chapter:Int,val verse:Int,val color:Int,val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="reading_progress", primaryKeys=["planId","day"])
data class ReadingProgressEntity(val planId:String,val day:Int,val completed:Boolean,val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="history")
data class HistoryEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val versionId:String,val bookId:String,val chapter:Int,val timestamp:Long=System.currentTimeMillis())
