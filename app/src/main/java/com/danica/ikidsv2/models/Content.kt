package com.danica.ikidsv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Content(
    val image : String  ? = null,
    val description : List<String> ? = null,
) : Parcelable
