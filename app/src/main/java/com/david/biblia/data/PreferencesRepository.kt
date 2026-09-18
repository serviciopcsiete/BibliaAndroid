package com.david.biblia.data
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
private val Context.ds by preferencesDataStore("reader_preferences")
data class ReaderPrefs(val version:String="rvr1960",val fontSize:Int=20,val theme:String="sepia",val pageMode:Boolean=true,val book:String="GEN",val chapter:Int=1)
class PreferencesRepository(private val context:Context){
 private object K{val version=stringPreferencesKey("version");val font=intPreferencesKey("font");val theme=stringPreferencesKey("theme");val page=booleanPreferencesKey("page");val book=stringPreferencesKey("book");val chapter=intPreferencesKey("chapter")}
 val flow=context.ds.data.map{ReaderPrefs(it[K.version]?:"rvr1960",it[K.font]?:20,it[K.theme]?:"sepia",it[K.page]?:true,it[K.book]?:"GEN",it[K.chapter]?:1)}
 suspend fun update(v:ReaderPrefs)=context.ds.edit{it[K.version]=v.version;it[K.font]=v.fontSize;it[K.theme]=v.theme;it[K.page]=v.pageMode;it[K.book]=v.book;it[K.chapter]=v.chapter}
}
