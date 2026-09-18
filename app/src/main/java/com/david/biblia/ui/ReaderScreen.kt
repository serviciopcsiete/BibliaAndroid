package com.david.biblia.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.david.biblia.data.BibleBlock
import com.david.biblia.data.Verse
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    vm: AppViewModel,
    book: String,
    chapter: Int,
    onBack: () -> Unit,
    onInfo: () -> Unit
) {
    val prefs by vm.prefs.collectAsState()
    val bookData = vm.repo.books.find { it.id == book }
    var blocks by remember(prefs.version, book, chapter) {
        mutableStateOf(vm.repo.chapterBlocks(prefs.version, book, chapter))
    }
    var selected by remember { mutableStateOf<Verse?>(null) }

    LaunchedEffect(book, chapter, prefs.version) {
        blocks = vm.repo.chapterBlocks(prefs.version, book, chapter)
        vm.updatePrefs { it.copy(book = book, chapter = chapter) }
        vm.history(prefs.version, book, chapter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("${bookData?.name ?: book} $chapter")
                        Text(
                            vm.repo.versions.find { it.id == prefs.version }?.abbreviation ?: prefs.version,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = onInfo) {
                        Icon(Icons.Default.Info, contentDescription = "Información del libro")
                    }
                }
            )
        }
    ) { padding ->
        when {
            blocks.isEmpty() -> {
                Box(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No hay texto disponible para este capítulo.")
                        Text(
                            vm.repo.versions.find { it.id == prefs.version }?.name ?: prefs.version,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            prefs.pageMode -> PagedReader(
                blocks = blocks,
                version = prefs.version,
                book = book,
                chapter = chapter,
                font = prefs.fontSize,
                modifier = Modifier.padding(padding),
                onVerse = { selected = it }
            )
            else -> ScrollReader(
                blocks = blocks,
                version = prefs.version,
                book = book,
                chapter = chapter,
                font = prefs.fontSize,
                modifier = Modifier.padding(padding),
                onVerse = { selected = it }
            )
        }
    }

    selected?.let { verse ->
        VerseActions(verse = verse, onDismiss = { selected = null }, vm = vm)
    }
}

@Composable
private fun ScrollReader(
    blocks: List<BibleBlock>,
    version: String,
    book: String,
    chapter: Int,
    font: Int,
    modifier: Modifier,
    onVerse: (Verse) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 18.dp)
    ) {
        items(blocks) { block -> BibleBlockRow(block, version, book, chapter, font, onVerse) }
    }
}

@Composable
private fun PagedReader(
    blocks: List<BibleBlock>,
    version: String,
    book: String,
    chapter: Int,
    font: Int,
    modifier: Modifier,
    onVerse: (Verse) -> Unit
) {
    val perPage = (36 - (font - 14)).coerceIn(10, 28)
    val pages = blocks.chunked(perPage)
    val state = rememberPagerState(pageCount = { pages.size })

    HorizontalPager(state = state, modifier = modifier.fillMaxSize()) { page ->
        val offset = ((state.currentPage - page) + state.currentPageOffsetFraction).coerceIn(-1f, 1f)
        Card(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = offset * 18f
                    cameraDistance = 18f * density
                    alpha = 1f - (offset.absoluteValue * 0.12f)
                }
        ) {
            LazyColumn(contentPadding = PaddingValues(22.dp)) {
                items(pages[page]) { block ->
                    BibleBlockRow(block, version, book, chapter, font, onVerse)
                }
                item {
                    Text(
                        text = "${page + 1} / ${pages.size}",
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun BibleBlockRow(
    block: BibleBlock,
    version: String,
    book: String,
    chapter: Int,
    font: Int,
    onVerse: (Verse) -> Unit
) {
    when(block.type) {
        "heading" -> Text(
            block.text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 8.dp)
        )
        "section" -> Text(
            block.text,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
        )
        "label" -> Text(
            block.text,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
        )
        else -> {
            val verseNumber = block.verse ?: return
            val verse = Verse(version, book, chapter, verseNumber, block.text)
            Text(
                text = buildAnnotatedString {
                    withStyle(MaterialTheme.typography.labelSmall.toSpanStyle().copy(fontWeight = FontWeight.Bold)) {
                        append("$verseNumber  ")
                    }
                    append(block.text)
                },
                fontSize = font.sp,
                lineHeight = (font * 1.55f).sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVerse(verse) }
                    .padding(vertical = 5.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerseActions(
    verse: Verse,
    onDismiss: () -> Unit,
    vm: AppViewModel
) {
    val context = LocalContext.current
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(20.dp)) {
            val bookName = vm.repo.books.find { it.id == verse.bookId }?.name ?: verse.bookId
            Text("$bookName ${verse.chapter}:${verse.verse}", fontWeight = FontWeight.Bold)
            Text(verse.text, Modifier.padding(vertical = 12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { vm.toggleFavorite(verse) },
                    label = { Text("Favorito") },
                    leadingIcon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null) }
                )
                AssistChip(
                    onClick = {
                        val versionName = vm.repo.versions.find { it.id == verse.versionId }?.abbreviation ?: verse.versionId
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "${verse.text} — $bookName ${verse.chapter}:${verse.verse} ($versionName)")
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartir versículo"))
                    },
                    label = { Text("Compartir") },
                    leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                )
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                label = { Text("Nota personal") }
            )
            Button(
                onClick = {
                    if (note.isNotBlank()) vm.saveNote(verse, note)
                    onDismiss()
                },
                modifier = Modifier.padding(top = 10.dp)
            ) { Text("Guardar nota") }
            Spacer(Modifier.height(18.dp))
        }
    }
}
