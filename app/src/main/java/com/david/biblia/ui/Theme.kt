package com.david.biblia.ui
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val Sepia=lightColorScheme(primary=Color(0xFF6C584C),background=Color(0xFFF6F0E3),surface=Color(0xFFFFFBF2),onBackground=Color(0xFF2D2926))
private val Light=lightColorScheme()
private val Dark=darkColorScheme()
@Composable fun AppTheme(mode:String,content:@Composable()->Unit){MaterialTheme(colorScheme=when(mode){"dark"->Dark;"light"->Light;else->Sepia},typography=Typography(),content=content)}
