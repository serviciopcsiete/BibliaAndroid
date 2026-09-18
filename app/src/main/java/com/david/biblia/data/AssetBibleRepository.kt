package com.david.biblia.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.Normalizer

class AssetBibleRepository(private val context:Context) {
    private fun read(path:String)=context.assets.open(path).bufferedReader().use{it.readText()}

    val versions:List<BibleVersion> by lazy { parseVersions(read("catalog/versions.json")) }
    val books:List<Book> by lazy { parseBooks(read("catalog/books.json")) }
    val bookInfo:List<BookInfo> by lazy { parseBookInfo(read("catalog/book_info.json")) }
    val plans:List<ReadingPlan> by lazy { parsePlans(read("plans/plans.json")) }
    val planEntries:List<PlanEntry> by lazy { parsePlanEntries(read("plans/entries.json")) }
    val topics:List<Topic> by lazy { parseTopics(read("topics/topics.json")) }

    private data class VersionData(
        val verses: List<Verse>,
        val blocks: Map<String,List<BibleBlock>>
    )

    private val cache=mutableMapOf<String,VersionData>()

    fun chapter(version:String,book:String,chapter:Int):List<Verse> =
        versionData(version).verses.filter{it.bookId==book && it.chapter==chapter}

    fun chapterBlocks(version:String,book:String,chapter:Int):List<BibleBlock> =
        versionData(version).blocks["$book.$chapter"].orEmpty()

    fun verse(version:String, book:String, chapter:Int, verse:Int):Verse? =
        versionData(version).verses.firstOrNull {
            it.bookId == book && it.chapter == chapter && it.verse == verse
        }

    fun search(version:String, query:String, limit:Int=200):List<Verse>{
        val normalized=norm(query)
        if(normalized.isBlank()) return emptyList()
        return versionData(version).verses.asSequence()
            .filter{norm(it.text).contains(normalized)}
            .take(limit)
            .toList()
    }

    fun available(version:String)=runCatching{
        context.assets.open("bibles/$version.json").close()
        true
    }.getOrDefault(false)

    private fun versionData(id:String):VersionData = cache.getOrPut(id){ loadVersion(id) }

    private fun loadVersion(id:String):VersionData{
        if(!available(id)) return VersionData(emptyList(), emptyMap())
        val source=read("bibles/$id.json").trim()
        return if(source.startsWith("{")) loadRichVersion(id, JSONObject(source)) else loadLegacyVersion(id, JSONArray(source))
    }

    private fun loadLegacyVersion(id:String,a:JSONArray):VersionData{
        val verses=ArrayList<Verse>(a.length())
        val blocks=linkedMapOf<String,MutableList<BibleBlock>>()
        for(i in 0 until a.length()){
            val o=a.getJSONObject(i)
            val verse=Verse(id,o.getString("book"),o.getInt("chapter"),o.getInt("verse"),o.getString("text"))
            verses+=verse
            blocks.getOrPut("${verse.bookId}.${verse.chapter}"){ mutableListOf() }
                .add(BibleBlock("verse",verse.verse,verse.text))
        }
        return VersionData(verses,blocks)
    }

    private fun loadRichVersion(id:String,root:JSONObject):VersionData{
        val versesByKey=linkedMapOf<String,Verse>()
        val blocks=linkedMapOf<String,MutableList<BibleBlock>>()
        val seenChapters=hashSetOf<String>()
        val booksArray=root.optJSONArray("books") ?: JSONArray()

        for(b in 0 until booksArray.length()){
            val bookObject=booksArray.getJSONObject(b)
            val bookUsfm=bookObject.getString("book_usfm")
            val chapters=bookObject.optJSONArray("chapters") ?: JSONArray()

            for(c in 0 until chapters.length()){
                val chapterObject=chapters.getJSONObject(c)
                val chapterUsfm=chapterObject.getString("chapter_usfm")
                if(!seenChapters.add(chapterUsfm)) continue
                val chapterNumber=chapterUsfm.substringAfterLast('.').toIntOrNull() ?: continue
                val chapterBlocks=blocks.getOrPut("$bookUsfm.$chapterNumber"){ mutableListOf() }
                val items=chapterObject.optJSONArray("items") ?: JSONArray()

                for(i in 0 until items.length()){
                    val item=items.getJSONObject(i)
                    val type=item.optString("type","verse")
                    val lines=item.optJSONArray("lines") ?: JSONArray()
                    val text=(0 until lines.length()).joinToString("\n"){lines.optString(it)}.trim()
                    if(text.isBlank()) continue

                    when(type){
                        "verse" -> {
                            val numbers=item.optJSONArray("verse_numbers") ?: JSONArray()
                            val verseNumber=if(numbers.length()>0) numbers.optInt(0,0) else 0
                            if(verseNumber<=0) continue
                            chapterBlocks += BibleBlock("verse",verseNumber,text)
                            val key="$bookUsfm.$chapterNumber.$verseNumber"
                            val existing=versesByKey[key]
                            versesByKey[key]=if(existing==null){
                                Verse(id,bookUsfm,chapterNumber,verseNumber,text)
                            }else{
                                existing.copy(text=(existing.text+"\n"+text).trim())
                            }
                        }
                        "heading1","heading2","heading3" -> chapterBlocks += BibleBlock("heading",null,text)
                        "label" -> chapterBlocks += BibleBlock("label",null,text)
                        "section1","section2" -> chapterBlocks += BibleBlock("section",null,text)
                        else -> chapterBlocks += BibleBlock("label",null,text)
                    }
                }
            }
        }
        return VersionData(versesByKey.values.toList(),blocks)
    }

    private fun norm(s:String)=Normalizer.normalize(s.lowercase(),Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(),"")

    private fun parseVersions(s:String):List<BibleVersion>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{BibleVersion(it.getString("id"),it.getString("name"),it.getString("abbreviation"),it.getString("language"),it.getString("license"))}}
    private fun parseBooks(s:String):List<Book>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{Book(it.getString("id"),it.getString("name"),it.getString("shortName"),it.getString("testament"),it.getInt("order"),it.getInt("chapters"))}}
    private fun parseBookInfo(s:String):List<BookInfo>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{ o->BookInfo(o.getString("bookId"),o.getString("author"),o.getString("date"),o.getString("context"),o.getString("purpose"),(0 until o.getJSONArray("themes").length()).map{o.getJSONArray("themes").getString(it)},(0 until o.getJSONArray("outline").length()).map{o.getJSONArray("outline").getString(it)})}}
    private fun parsePlans(s:String):List<ReadingPlan>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{ReadingPlan(it.getString("id"),it.getString("name"),it.getString("description"),it.getInt("totalDays"))}}
    private fun parsePlanEntries(s:String):List<PlanEntry>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{o->PlanEntry(o.getString("planId"),o.getInt("day"),(0 until o.getJSONArray("passages").length()).map{o.getJSONArray("passages").getString(it)})}}
    private fun parseTopics(s:String):List<Topic>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{o->Topic(o.getString("id"),o.getString("name"),(0 until o.getJSONArray("references").length()).map{o.getJSONArray("references").getString(it)})}}
}
