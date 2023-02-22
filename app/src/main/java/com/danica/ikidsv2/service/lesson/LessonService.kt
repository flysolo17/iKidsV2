package com.danica.ikidsv2.service.lesson

import android.net.Uri
import com.danica.ikidsv2.models.Content
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.utils.UiState

interface LessonService {
     fun createLesson(lesson : Lessons,result : (UiState<Boolean>) -> Unit)
     fun deleteLesson(id : String,result : (UiState<Boolean>) -> Unit)
     fun getAllLessons(teacherID : String,result: (UiState<List<Lessons>>) -> Unit)
     fun getAllLessonsByClass(classID : String,result: (UiState<List<Lessons>>) -> Unit)
     fun getLessonByID(lessonID : String,result: (UiState<Lessons>) -> Unit)
     fun updateLesson( id : String,title : String,desc : String,result : (UiState<Boolean>) -> Unit)
     fun uploadImage(imageUri: Uri, lessonID: String, result: (UiState<Uri>) -> Unit)
     fun updateImage(imageLink : String , lessonID: String,result : (UiState<Boolean>) -> Unit)
     fun updateAvailability(lessonID: String,isOpen : Boolean)
     fun addLessonContent(lessonID: String,content : Content,result: (UiState<Boolean>) -> Unit)
     fun deleteContent(lessonID: String,contents : List<Content>,result: (UiState<String>) -> Unit)
}