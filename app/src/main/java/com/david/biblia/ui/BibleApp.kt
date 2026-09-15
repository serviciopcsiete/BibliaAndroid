package com.david.biblia.ui
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*

@Composable fun BibleApp(vm:AppViewModel= viewModel()){
 val prefs by vm.prefs.collectAsState()
 AppTheme(prefs.theme){
  val nav=rememberNavController(); val back by nav.currentBackStackEntryAsState(); val route=back?.destination?.route
  Scaffold(bottomBar={ if(route?.startsWith("reader")!=true) NavigationBar{
   listOf("home" to Icons.Default.Home,"bible" to Icons.Default.MenuBook,"plans" to Icons.Default.CalendarMonth,"topics" to Icons.Default.FavoriteBorder,"settings" to Icons.Default.Settings).forEach{(r,i)->NavigationBarItem(selected=route==r,onClick={nav.navigate(r){launchSingleTop=true;restoreState=true}},icon={Icon(i,null)},label={Text(when(r){"home"->"Inicio";"bible"->"Biblia";"plans"->"Planes";"topics"->"Temas";else->"Ajustes"})})}
  }}){pad->NavHost(nav,"home",Modifier.padding(pad)){
   composable("home"){HomeScreen(vm){b,c->nav.navigate("reader/$b/$c")}}
   composable("bible"){LibraryScreen(vm){b,c->nav.navigate("reader/$b/$c")}}
   composable("plans"){PlansScreen(vm){ref->nav.navigate("search?initial=$ref")}}
   composable("topics"){TopicsScreen(vm){ref->nav.navigate("search?initial=$ref")}}
   composable("settings"){SettingsScreen(vm)}
   composable("favorites"){FavoritesScreen(vm){b,c->nav.navigate("reader/$b/$c")}}
   composable("search?initial={initial}",arguments=listOf(navArgument("initial"){type=NavType.StringType;defaultValue=""})){SearchScreen(vm,it.arguments?.getString("initial")?:""){b,c->nav.navigate("reader/$b/$c")}}
   composable("bookinfo/{book}"){BookInfoScreen(vm,it.arguments?.getString("book")?:"GEN"){b->nav.navigate("reader/$b/1")}}
   composable("reader/{book}/{chapter}",arguments=listOf(navArgument("chapter"){type=NavType.IntType})){ReaderScreen(vm,it.arguments?.getString("book")?:"GEN",it.arguments?.getInt("chapter")?:1,onBack={nav.popBackStack()},onInfo={nav.navigate("bookinfo/${it.arguments?.getString("book")?:"GEN"}")})}
  }}
 }
}
