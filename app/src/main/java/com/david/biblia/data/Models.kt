package com.david.biblia.data

data class BibleVersion(val id:String,val name:String,val abbreviation:String,val language:String,val license:String)
data class Book(val id:String,val name:String,val shortName:String,val testament:String,val order:Int,val chapters:Int)
data class Verse(val versionId:String,val bookId:String,val chapter:Int,val verse:Int,val text:String)
data class BookInfo(val bookId:String,val author:String,val date:String,val context:String,val purpose:String,val themes:List<String>,val outline:List<String>)
data class ReadingPlan(val id:String,val name:String,val description:String,val totalDays:Int)
data class PlanEntry(val planId:String,val day:Int,val passages:List<String>)
data class Topic(val id:String,val name:String,val references:List<String>)
