package com.danica.ikidsv2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Score(
    var id: String? = null,
    val lessonID: String ? = null,
    val studentID: String ? = null,
    val score: Int? = null,
    val date: Long ? = null
) : Parcelable {

}