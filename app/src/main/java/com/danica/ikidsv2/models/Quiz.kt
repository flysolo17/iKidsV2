package com.danica.ikidsv2.models

data class Quiz(
    var id : String ? = null,
    val lessonID : String ?= null,
    var image : String? = null,
    val question : String ? = null,
    val answer : String ? = null,
    val choices : List<String> ? = null,
    val createdAt : Long ? = null,
) {

}