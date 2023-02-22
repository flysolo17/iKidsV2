package com.danica.ikidsv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Classes(
    var id : String ? = null,
    val name : String ? = null,
    val code : String ? = null,
    val students : List<String> ? = null,
    val teacherID : String? = null,
    val createdAt : Long ? = null,
) : Parcelable
