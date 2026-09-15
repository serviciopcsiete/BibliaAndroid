package com.david.biblia.data
import android.content.Context
import org.json.JSONArray
import java.text.Normalizer

class AssetBibleRepository(private val context:Context) {
 private fun read(path:String)=context.assets.open(path).bufferedReader().use{it.readText()}
 val versions:List<BibleVersion> by lazy { parseVersions(read("catalog/versions.json")) }
 val books:List<Book> by lazy { parseBooks(read("catalog/books.json")) }
 val bookInfo:List<BookInfo> by lazy { parseBookInfo(read("catalog/book_info.json")) }
 val plans:List<ReadingPlan> by lazy { parsePlans(read("plans/plans.json")) }
 val planEntries:List<PlanEntry> by lazy { parsePlanEntries(read("plans/entries.json")) }
 val topics:List<Topic> by lazy { parseTopics(read("topics/topics.json")) }
 private val cache=mutableMapOf<String,List<Verse>>()

 fun chapter(version:String,book:String,chapter:Int):List<Verse> = cache.getOrPut(version){ loadVersion(version) }.filter{it.bookId==book && it.chapter==chapter}
 fun search(version:String, query:String, limit:Int=200):List<Verse>{
  val normalized=norm(query); if(normalized.isBlank()) return emptyList()
  return cache.getOrPut(version){loadVersion(version)}.asSequence().filter{norm(it.text).contains(normalized)}.take(limit).toList()
 }
 fun available(version:String)=runCatching{context.assets.open("bibles/$version.json").close();true}.getOrDefault(false)
 private fun loadVersion(id:String):List<Verse>{
   if(!available(id)) return emptyList()
   val a=JSONArray(read("bibles/$id.json")); val out=ArrayList<Verse>(a.length())
   for(i in 0 until a.length()){ val o=a.getJSONObject(i); out+=Verse(id,o.getString("book"),o.getInt("chapter"),o.getInt("verse"),o.getString("text")) }
   return out
 }
 private fun norm(s:String)=Normalizer.normalize(s.lowercase(),Normalizer.Form.NFD).replace("\\p{Mn}+".toRegex(),"")
 private fun parseVersions(s:String):List<BibleVersion>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{BibleVersion(it.getString("id"),it.getString("name"),it.getString("abbreviation"),it.getString("language"),it.getString("license"))}}
 private fun parseBooks(s:String):List<Book>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{Book(it.getString("id"),it.getString("name"),it.getString("shortName"),it.getString("testament"),it.getInt("order"),it.getInt("chapters"))}}
 private fun parseBookInfo(s:String):List<BookInfo>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{ o->BookInfo(o.getString("bookId"),o.getString("author"),o.getString("date"),o.getString("context"),o.getString("purpose"),(0 until o.getJSONArray("themes").length()).map{o.getJSONArray("themes").getString(it)},(0 until o.getJSONArray("outline").length()).map{o.getJSONArray("outline").getString(it)})}}
 private fun parsePlans(s:String):List<ReadingPlan>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{ReadingPlan(it.getString("id"),it.getString("name"),it.getString("description"),it.getInt("totalDays"))}}
 private fun parsePlanEntries(s:String):List<PlanEntry>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{o->PlanEntry(o.getString("planId"),o.getInt("day"),(0 until o.getJSONArray("passages").length()).map{o.getJSONArray("passages").getString(it)})}}
 private fun parseTopics(s:String):List<Topic>{val a=JSONArray(s);return (0 until a.length()).map{a.getJSONObject(it)}.map{o->Topic(o.getString("id"),o.getString("name"),(0 until o.getJSONArray("references").length()).map{o.getJSONArray("references").getString(it)})}}
}
