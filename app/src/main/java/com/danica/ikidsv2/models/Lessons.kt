package com.danica.ikidsv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lessons(
    var id : String ? = null,
    val teacherID : String ? = null,
    val image : String ? = null,
    val title : String  ? = null,
    val description : String ? = null,
    val content: List<Content>  ? = null,
    val classes: List<String> ? = null,
    @field:JvmField
    var isOpen : Boolean   = false,
    val createdAt : Long ? = null,
) : Parcelable {

}