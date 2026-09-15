package com.david.biblia.ui
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.david.biblia.data.Verse
import kotlin.math.absoluteValue

@Composable fun ReaderScreen(vm:AppViewModel,book:String,chapter:Int,onBack:()->Unit,onInfo:()->Unit){
 val p by vm.prefs.collectAsState();val b=vm.repo.books.find{it.id==book};var verses by remember(p.version,book,chapter){mutableStateOf(vm.repo.chapter(p.version,book,chapter))};var selected by remember{mutableStateOf<Verse?>(null)}
 LaunchedEffect(book,chapter,p.version){verses=vm.repo.chapter(p.version,book,chapter);vm.updatePrefs{it.copy(book=book,chapter=chapter)};vm.history(p.version,book,chapter)}
 Scaffold(topBar={TopAppBar(title={Text("${b?.name?:book} $chapter")},navigationIcon={IconButton(onBack){Icon(Icons.Default.ArrowBack,null)}},actions={IconButton(onInfo){Icon(Icons.Default.Info,null)}})}){pad->
  if(verses.isEmpty()) Box(Modifier.padding(pad).fillMaxSize(),contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Text("Texto bíblico no instalado para ${p.version}.");Text("Usa tools/import_usfm.py para generar el corpus oficial.",style=MaterialTheme.typography.bodySmall)}}
  else if(p.pageMode) PagedReader(verses,p.fontSize,Modifier.padding(pad)){selected=it} else ScrollReader(verses,p.fontSize,Modifier.padding(pad)){selected=it}
 }
 selected?.let{v->VerseActions(v,onDismiss={selected=null},vm=vm)}
}
@Composable private fun ScrollReader(verses:List<Verse>,font:Int,modifier:Modifier,onVerse:(Verse)->Unit){LazyColumn(modifier.fillMaxSize(),contentPadding=PaddingValues(22.dp)){items(verses){v->VerseRow(v,font,onVerse)}}}
@Composable private fun PagedReader(verses:List<Verse>,font:Int,modifier:Modifier,onVerse:(Verse)->Unit){
 val perPage=(42-(font-14)).coerceIn(12,32);val pages=verses.chunked(perPage);val state=rememberPagerState{pages.size}
 HorizontalPager(state,modifier.fillMaxSize()){page->val offset=((state.currentPage-page)+state.currentPageOffsetFraction).coerceIn(-1f,1f);Card(Modifier.padding(12.dp).fillMaxSize().graphicsLayer{rotationY=offset*18f;cameraDistance=18*density;alpha=1f-(offset.absoluteValue*.12f)}){LazyColumn(contentPadding=PaddingValues(22.dp)){items(pages[page]){v->VerseRow(v,font,onVerse)};item{Text("${page+1} / ${pages.size}",Modifier.fillMaxWidth().padding(16.dp),style=MaterialTheme.typography.labelSmall)}}}}
}
@Composable private fun VerseRow(v:Verse,font:Int,onVerse:(Verse)->Unit){Text(buildAnnotatedString{withStyle(MaterialTheme.typography.labelSmall.toSpanStyle().copy(fontWeight=FontWeight.Bold)){append("${v.verse}  ")};append(v.text)},fontSize=font.sp,lineHeight=(font*1.55f).sp,modifier=Modifier.fillMaxWidth().clickable{onVerse(v)}.padding(vertical=5.dp))}
@Composable private fun VerseActions(v:Verse,onDismiss:()->Unit,vm:AppViewModel){val ctx=LocalContext.current;var note by remember{mutableStateOf("")};ModalBottomSheet(onDismissRequest=onDismiss){Column(Modifier.padding(20.dp)){Text("${vm.repo.books.find{it.id==v.bookId}?.name} ${v.chapter}:${v.verse}",fontWeight=FontWeight.Bold);Text(v.text,Modifier.padding(vertical=12.dp));Row{AssistChip({vm.toggleFavorite(v)},{Text("Favorito")},leadingIcon={Icon(Icons.Default.FavoriteBorder,null)});Spacer(Modifier.width(8.dp));AssistChip({val i=Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_TEXT,"${v.text} — ${v.bookId} ${v.chapter}:${v.verse}")};ctx.startActivity(Intent.createChooser(i,"Compartir versículo"))},{Text("Compartir")},leadingIcon={Icon(Icons.Default.Share,null)})};OutlinedTextField(note,{note=it},Modifier.fillMaxWidth().padding(top=12.dp),label={Text("Nota personal")});Button({if(note.isNotBlank())vm.saveNote(v,note);onDismiss()},Modifier.padding(top=10.dp)){Text("Guardar nota")};Spacer(Modifier.height(18.dp))}}
