package com.danica.ikidsv2.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap
import java.text.SimpleDateFormat
import java.util.*

fun dateFormat(timestamp : Long ) : String {
    val dateFormated = SimpleDateFormat("MM/dd/yy")
    return dateFormated.format(Date(timestamp))
}
fun dateFormat2(timestamp : Long ) : String {
    val dateFormated = SimpleDateFormat("mmm dd yyyy")
    return dateFormated.format(Date(timestamp))
}
//TODO: get the file extension of the file
fun Activity.getFileExtension(uri: Uri): String? {
    val cR = this.contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(uri))
}
class Constants {

    companion object {
        const val USER_TABLE = "Users"
        const val CLASS_CODES_TABLE = "ClassCodes"
        const val LESSONS_TABLE = "Lessons"
        const val QUIZ_TABLE = "Quiz"
        const val SCORES_TABLE = "Scores"
        const val CLASSES_TABLE = "Classes"
    }
}