package com.david.biblia.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun BibleApp(vm: AppViewModel = viewModel()) {
    val prefs by vm.prefs.collectAsState()

    AppTheme(prefs.theme) {
        val nav = rememberNavController()
        val backStackEntry by nav.currentBackStackEntryAsState()
        val route = backStackEntry?.destination?.route

        Scaffold(
            bottomBar = {
                if (route?.startsWith("reader") != true) {
                    NavigationBar {
                        val items = listOf(
                            Triple("home", Icons.Default.Home, "Inicio"),
                            Triple("bible", Icons.Default.MenuBook, "Biblia"),
                            Triple("plans", Icons.Default.CalendarMonth, "Planes"),
                            Triple("topics", Icons.Default.FavoriteBorder, "Temas"),
                            Triple("settings", Icons.Default.Settings, "Ajustes")
                        )
                        items.forEach { (destination, icon, label) ->
                            NavigationBarItem(
                                selected = route == destination,
                                onClick = {
                                    nav.navigate(destination) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(icon, contentDescription = null) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = "home",
                modifier = Modifier.padding(padding)
            ) {
                composable("home") {
                    HomeScreen(vm) { book, chapter -> nav.navigate("reader/$book/$chapter") }
                }
                composable("bible") {
                    LibraryScreen(vm) { book, chapter -> nav.navigate("reader/$book/$chapter") }
                }
                composable("plans") {
                    PlansScreen(vm) { reference -> nav.navigate("search?initial=$reference") }
                }
                composable("topics") {
                    TopicsScreen(vm) { reference -> nav.navigate("search?initial=$reference") }
                }
                composable("settings") { SettingsScreen(vm) }
                composable("favorites") {
                    FavoritesScreen(vm) { book, chapter -> nav.navigate("reader/$book/$chapter") }
                }
                composable(
                    route = "search?initial={initial}",
                    arguments = listOf(
                        navArgument("initial") {
                            type = NavType.StringType
                            defaultValue = ""
                        }
                    )
                ) { entry ->
                    SearchScreen(vm, entry.arguments?.getString("initial").orEmpty()) { book, chapter ->
                        nav.navigate("reader/$book/$chapter")
                    }
                }
                composable("bookinfo/{book}") { entry ->
                    val book = entry.arguments?.getString("book") ?: "GEN"
                    BookInfoScreen(vm, book) { selectedBook ->
                        nav.navigate("reader/$selectedBook/1")
                    }
                }
                composable(
                    route = "reader/{book}/{chapter}",
                    arguments = listOf(
                        navArgument("book") { type = NavType.StringType },
                        navArgument("chapter") { type = NavType.IntType }
                    )
                ) { entry ->
                    val book = entry.arguments?.getString("book") ?: "GEN"
                    val chapter = entry.arguments?.getInt("chapter") ?: 1
                    ReaderScreen(
                        vm = vm,
                        book = book,
                        chapter = chapter,
                        onBack = { nav.popBackStack() },
                        onInfo = { nav.navigate("bookinfo/$book") }
                    )
                }
            }
        }
    }
}
