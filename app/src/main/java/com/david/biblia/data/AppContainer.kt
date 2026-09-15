package com.david.biblia.data
import android.content.Context
import androidx.room.Room
class AppContainer(context:Context){
 val bible=AssetBibleRepository(context)
 val db=Room.databaseBuilder(context,AppDatabase::class.java,"biblia_user.db").build()
 val prefs=PreferencesRepository(context)
}
