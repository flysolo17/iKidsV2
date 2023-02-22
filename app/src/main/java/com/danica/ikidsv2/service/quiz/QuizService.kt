package com.danica.ikidsv2.service.quiz

import android.net.Uri
import com.danica.ikidsv2.models.Quiz
import com.danica.ikidsv2.utils.UiState

interface QuizService {
    fun addQuiz(quiz : Quiz,result : (UiState<String>) -> Unit)
    fun getQuiz(lessonID : String,result: (UiState<List<Quiz>>) -> Unit)
    fun updateQuiz(quizID : String,result: (UiState<String>) -> Unit)
    fun deleteQuiz(quizID : String, result: (UiState<String>) -> Unit)
    fun uploadImage(imageUri: Uri, lessonID: String, result: (UiState<Uri>) -> Unit)

}