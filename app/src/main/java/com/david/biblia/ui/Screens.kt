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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
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
import java.time.LocalDate

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
    val dailyReferences = remember {
        listOf(
            Triple("PSA", 23, 1), Triple("PHP", 4, 13), Triple("ISA", 41, 10),
            Triple("JHN", 3, 16), Triple("PRO", 3, 5), Triple("ROM", 8, 28),
            Triple("PSA", 46, 1), Triple("MAT", 11, 28), Triple("JOS", 1, 9),
            Triple("PSA", 121, 1), Triple("1PE", 5, 7), Triple("JHN", 14, 27),
            Triple("PSA", 34, 8), Triple("LAM", 3, 22), Triple("HEB", 11, 1),
            Triple("ISA", 40, 31), Triple("PSA", 37, 5), Triple("ROM", 12, 12),
            Triple("2CO", 5, 7), Triple("PSA", 119, 105), Triple("GAL", 5, 22),
            Triple("EPH", 6, 10), Triple("COL", 3, 15), Triple("1CO", 13, 13),
            Triple("PSA", 55, 22), Triple("MAT", 6, 33), Triple("ROM", 15, 13),
            Triple("PSA", 91, 1), Triple("JHN", 8, 12), Triple("DEU", 31, 8),
            Triple("PSA", 118, 24)
        )
    }
    val todayRef = dailyReferences[(LocalDate.now().dayOfYear - 1) % dailyReferences.size]
    val dailyVerse = remember(prefs.version, todayRef) {
        vm.repo.verse(prefs.version, todayRef.first, todayRef.second, todayRef.third)
    }
    val dailyBookName = vm.repo.books.find { it.id == todayRef.first }?.name ?: todayRef.first

    LazyColumn(Modifier.fillMaxSize()) {
        item { Header("Biblia", "Lectura, estudio y planes") }
        item {
            Card(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .clickable { open(todayRef.first, todayRef.second) }
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.MenuBook, contentDescription = null)
                        Text("Texto del día", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        dailyVerse?.text ?: "Abre la Biblia para comenzar tu lectura de hoy.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "$dailyBookName ${todayRef.second}:${todayRef.third} · RVR1960",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
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
                "Reina-Valera 1960 disponible sin conexión.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private data class BibleSection(val title: String, val range: IntRange)

private val bibleSections = listOf(
    BibleSection("Pentateuco", 1..5),
    BibleSection("Históricos", 6..17),
    BibleSection("Poéticos y sapienciales", 18..22),
    BibleSection("Profetas mayores", 23..27),
    BibleSection("Profetas menores", 28..39),
    BibleSection("Evangelios", 40..43),
    BibleSection("Historia", 44..44),
    BibleSection("Cartas paulinas", 45..57),
    BibleSection("Cartas generales", 58..65),
    BibleSection("Profecía", 66..66)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    vm: AppViewModel,
    openBook: (String) -> Unit,
    openInfo: (String) -> Unit
) {
    var testament by remember { mutableStateOf("OT") }
    val visibleSections = if (testament == "OT") bibleSections.take(5) else bibleSections.drop(5)

    Column {
        Header("Biblioteca", "Selecciona un libro")
        SingleChoiceSegmentedButtonRow(Modifier.padding(horizontal = 16.dp)) {
            listOf("OT" to "Antiguo", "NT" to "Nuevo").forEachIndexed { index, item ->
                SegmentedButton(
                    selected = testament == item.first,
                    onClick = { testament = item.first },
                    shape = SegmentedButtonDefaults.itemShape(index, 2)
                ) { Text(item.second) }
            }
        }
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            visibleSections.forEach { section ->
                val sectionBooks = vm.repo.books
                    .filter { it.order in section.range }
                    .sortedBy { it.order }
                item(key = "section-${section.title}") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(section.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
                items(sectionBooks, key = { it.id }) { book ->
                    ListItem(
                        headlineContent = { Text(book.name) },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { openInfo(book.id) }) {
                                    Icon(Icons.Default.Info, contentDescription = "Información de ${book.name}")
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        },
                        modifier = Modifier.clickable { openBook(book.id) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ChapterPickerScreen(vm: AppViewModel, bookId: String, openChapter: (Int) -> Unit) {
    val book = vm.repo.books.find { it.id == bookId }
    if (book == null) {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Text("Libro no encontrado")
        }
        return
    }

    Column(Modifier.fillMaxSize()) {
        Header(book.name, "Selecciona un capítulo")
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            gridItems((1..book.chapters).toList()) { chapter ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openChapter(chapter) }
                ) {
                    Text(
                        text = chapter.toString(),
                        modifier = Modifier.padding(vertical = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
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
fun BookInfoScreen(vm: AppViewModel, book: String, openChapter: (Int) -> Unit) {
    val bookData = vm.repo.books.find { it.id == book }
    val info = vm.repo.bookInfo.find { it.bookId == book }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp)
    ) {
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
                InfoBlock("Estructura", details.outline.joinToString("
"))
            }
        }
        bookData?.let { currentBook ->
            item {
                Spacer(Modifier.height(24.dp))
                Text("Capítulos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "Toca un número para ir directamente al capítulo.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                (1..currentBook.chapters).toList().chunked(5).forEach { rowChapters ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowChapters.forEach { chapter ->
                            Button(
                                onClick = { openChapter(chapter) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) { Text(chapter.toString()) }
                        }
                        repeat(5 - rowChapters.size) { Spacer(Modifier.weight(1f)) }
                    }
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
        Text("Versión bíblica", Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
        ListItem(
            headlineContent = { Text("Biblia Reina-Valera 1960") },
            supportingContent = { Text("RVR1960 · Español (América Latina)") },
            leadingContent = { Icon(Icons.Default.MenuBook, contentDescription = null) }
        )

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
