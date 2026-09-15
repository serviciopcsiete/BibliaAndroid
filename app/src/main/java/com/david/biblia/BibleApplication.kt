package com.david.biblia
import android.app.Application
import com.david.biblia.data.AppContainer
class BibleApplication: Application() { val container by lazy { AppContainer(this) } }
