package com.david.biblia.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
private fun Header(title: String, subtitle: String? = null) {
    Column(Modifier.padding(20.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )
        subtitle?.let {
            Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun HomeScreen(vm: AppViewModel, open: (String, Int) -> Unit) {
    val prefs by vm.prefs.collectAsState()
    LazyColumn(Modifier.fillMaxSize()) {
        item { Header("Biblia", "Lectura, estudio y planes") }
        item {
            Card(Modifier.padding(16.dp).fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("Continuar leyendo", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${vm.repo.books.find { it.id == prefs.book }?.name ?: prefs.book} ${prefs.chapter}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Button(onClick = { open(prefs.book, prefs.chapter) }) {
                        Text("Continuar")
                    }
                }
            }
        }
        item {
            Card(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("Plan anual", fontWeight = FontWeight.Bold)
                    Text("Avanza cada día y conserva tu progreso local.")
                    TextButton(onClick = {}) { Text("Disponible en Planes") }
                }
            }
        }
        item {
            Text(
                "Todo funciona sin conexión una vez instalado el corpus bíblico.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(vm: AppViewModel, open: (String, Int) -> Unit) {
    var testament by remember { mutableStateOf("OT") }
    Column {
        Header("Biblioteca")
        SingleChoiceSegmentedButtonRow(Modifier.padding(horizontal = 16.dp)) {
            listOf("OT" to "Antiguo", "NT" to "Nuevo").forEachIndexed { index, item ->
                SegmentedButton(
                    selected = testament == item.first,
                    onClick = { testament = item.first },
                    shape = SegmentedButtonDefaults.itemShape(index, 2)
                ) {
                    Text(item.second)
                }
            }
        }
        LazyColumn {
            items(vm.repo.books.filter { it.testament == testament }) { book ->
                ListItem(
                    headlineContent = { Text(book.name) },
                    supportingContent = { Text("${book.chapters} capítulos") },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { open(book.id, 1) }
                )
            }
        }
    }
}

@Composable
fun SearchScreen(vm: AppViewModel, initial: String, open: (String, Int) -> Unit) {
    val prefs by vm.prefs.collectAsState()
    var query by remember { mutableStateOf(initial) }
    var results by remember { mutableStateOf(emptyList<com.david.biblia.data.Verse>()) }

    Column {
        Header("Buscar")
        Row(Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Palabra o frase") }
            )
            IconButton(onClick = { results = vm.repo.search(prefs.version, query) }) {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            }
        }
        if (!vm.repo.available(prefs.version)) {
            Text(
                "El corpus ${prefs.version} aún no está instalado. Ejecuta tools/import_usfm.py.",
                Modifier.padding(16.dp)
            )
        }
        LazyColumn {
            items(results) { verse ->
                ListItem(
                    headlineContent = {
                        Text(
                            "${vm.repo.books.find { it.id == verse.bookId }?.name ?: verse.bookId} ${verse.chapter}:${verse.verse}"
                        )
                    },
                    supportingContent = { Text(verse.text, maxLines = 3) },
                    modifier = Modifier.clickable { open(verse.bookId, verse.chapter) }
                )
            }
        }
    }
}

@Composable
fun PlansScreen(vm: AppViewModel, openRef: (String) -> Unit) {
    var selected by remember { mutableStateOf(vm.repo.plans.firstOrNull()?.id.orEmpty()) }
    val progress = if (selected.isNotBlank()) {
        vm.dao.progress(selected).collectAsState(initial = emptyList()).value
    } else {
        emptyList()
    }

    Column {
        Header("Planes de lectura")
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vm.repo.plans) { plan ->
                FilterChip(
                    selected = selected == plan.id,
                    onClick = { selected = plan.id },
                    label = { Text(plan.name) }
                )
            }
        }
        LazyColumn {
            items(vm.repo.planEntries.filter { it.planId == selected }) { entry ->
                val done = progress.any { it.day == entry.day && it.completed }
                Card(Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row {
                            Checkbox(
                                checked = done,
                                onCheckedChange = { vm.setProgress(selected, entry.day, it) }
                            )
                            Text("Día ${entry.day}", fontWeight = FontWeight.Bold)
                        }
                        entry.passages.forEach { reference ->
                            TextButton(onClick = { openRef(reference) }) {
                                Text(reference)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopicsScreen(vm: AppViewModel, openRef: (String) -> Unit) {
    Column {
        Header("Para cada necesidad")
        LazyColumn {
            items(vm.repo.topics) { topic ->
                var expanded by remember(topic.id) { mutableStateOf(false) }
                Card(Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth()) {
                    Column {
                        ListItem(
                            headlineContent = { Text(topic.name) },
                            trailingContent = {
                                Icon(
                                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                        if (expanded) {
                            topic.references.forEach { reference ->
                                TextButton(
                                    onClick = { openRef(reference) },
                                    modifier = Modifier.padding(start = 12.dp)
                                ) {
                                    Text(reference)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(vm: AppViewModel, open: (String, Int) -> Unit) {
    val favorites by vm.favorites.collectAsState()
    Column {
        Header("Favoritos")
        LazyColumn {
            items(favorites) { favorite ->
                ListItem(
                    headlineContent = {
                        Text(
                            "${vm.repo.books.find { it.id == favorite.bookId }?.name ?: favorite.bookId} ${favorite.chapter}:${favorite.verse}"
                        )
                    },
                    modifier = Modifier.clickable { open(favorite.bookId, favorite.chapter) }
                )
            }
        }
    }
}

@Composable
fun BookInfoScreen(vm: AppViewModel, book: String, open: (String) -> Unit) {
    val bookData = vm.repo.books.find { it.id == book }
    val info = vm.repo.bookInfo.find { it.bookId == book }

    LazyColumn(Modifier.padding(20.dp)) {
        item {
            Text(
                bookData?.name ?: book,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
        info?.let { details ->
            item {
                InfoBlock("Autor", details.author)
                InfoBlock("Fecha", details.date)
                InfoBlock("Contexto", details.context)
                InfoBlock("Propósito", details.purpose)
                InfoBlock("Temas", details.themes.joinToString(" • "))
                InfoBlock("Estructura", details.outline.joinToString("\n"))
                Button(onClick = { open(book) }) {
                    Text("Comenzar lectura")
                }
            }
        }
    }
}

@Composable
private fun InfoBlock(title: String, value: String) {
    Spacer(Modifier.height(18.dp))
    Text(title, fontWeight = FontWeight.Bold)
    Text(value)
}

@Composable
fun SettingsScreen(vm: AppViewModel) {
    val prefs by vm.prefs.collectAsState()
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Header("Ajustes")
        Text("Versión", Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)

        vm.repo.versions.forEach { version ->
            ListItem(
                headlineContent = { Text(version.name) },
                supportingContent = {
                    Text(
                        if (vm.repo.available(version.id)) {
                            version.license
                        } else {
                            "Corpus no instalado • ${version.license}"
                        }
                    )
                },
                leadingContent = {
                    RadioButton(
                        selected = prefs.version == version.id,
                        onClick = { vm.updatePrefs { it.copy(version = version.id) } }
                    )
                }
            )
        }

        HorizontalDivider()
        Text("Tamaño de texto: ${prefs.fontSize}", Modifier.padding(16.dp))
        Slider(
            value = prefs.fontSize.toFloat(),
            onValueChange = { value -> vm.updatePrefs { it.copy(fontSize = value.toInt()) } },
            valueRange = 14f..30f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text("Tema", Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
        Row(
            Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("light" to "Claro", "sepia" to "Sepia", "dark" to "Oscuro").forEach { (id, name) ->
                FilterChip(
                    selected = prefs.theme == id,
                    onClick = { vm.updatePrefs { it.copy(theme = id) } },
                    label = { Text(name) }
                )
            }
        }

        ListItem(
            headlineContent = { Text("Cambio de hoja") },
            supportingContent = { Text("Animación horizontal tipo libro") },
            trailingContent = {
                Switch(
                    checked = prefs.pageMode,
                    onCheckedChange = { enabled -> vm.updatePrefs { it.copy(pageMode = enabled) } }
                )
            }
        )
    }
}
