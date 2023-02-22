package com.danica.ikidsv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id : String? = null,
    val email : String ? = null,
    val type : UserType ? = null,
    val name : String? = null,
    var gender: String? = null,
    val avatar : String ? = null,
    val createdAt : Long ? = null
) : Parcelable {
}