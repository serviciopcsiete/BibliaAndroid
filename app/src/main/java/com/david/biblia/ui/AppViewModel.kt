package com.david.biblia.ui
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.david.biblia.BibleApplication
import com.david.biblia.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(app:Application):AndroidViewModel(app){
 private val c=(app as BibleApplication).container
 val repo=c.bible; val dao=c.db.userDao(); val prefs=c.prefs.flow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReaderPrefs())
 val favorites=dao.favorites().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val notes=dao.notes().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 suspend fun verses(version:String,book:String,chapter:Int)=repo.chapter(version,book,chapter)
 fun updatePrefs(transform:(ReaderPrefs)->ReaderPrefs)=viewModelScope.launch{c.prefs.update(transform(prefs.value))}
 fun toggleFavorite(v:Verse)=viewModelScope.launch{val e=FavoriteEntity(v.versionId,v.bookId,v.chapter,v.verse);if(dao.favoriteCount(v.versionId,v.bookId,v.chapter,v.verse)>0)dao.removeFavorite(e) else dao.addFavorite(e)}
 fun saveNote(v:Verse,text:String)=viewModelScope.launch{dao.saveNote(NoteEntity(versionId=v.versionId,bookId=v.bookId,chapter=v.chapter,verse=v.verse,text=text))}
 fun highlight(v:Verse,color:Int)=viewModelScope.launch{dao.saveHighlight(HighlightEntity(v.versionId,v.bookId,v.chapter,v.verse,color))}
 fun history(version:String,book:String,chapter:Int)=viewModelScope.launch{dao.addHistory(HistoryEntity(versionId=version,bookId=book,chapter=chapter))}
 fun setProgress(plan:String,day:Int,done:Boolean)=viewModelScope.launch{dao.setProgress(ReadingProgressEntity(plan,day,done))}
}
