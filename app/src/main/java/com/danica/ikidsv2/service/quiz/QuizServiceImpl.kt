package com.danica.ikidsv2.service.quiz

import android.net.Uri
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.models.Quiz
import com.danica.ikidsv2.utils.Constants
import com.danica.ikidsv2.utils.UiState
import com.google.common.io.Files
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

class QuizServiceImpl(private val  firestore: FirebaseFirestore,val firebaseStorage: FirebaseStorage) : QuizService {
    override fun addQuiz(quiz: Quiz, result: (UiState<String>) -> Unit) {
        val id = firestore.collection(Constants.QUIZ_TABLE).document().id
        quiz.id = id
        result.invoke(UiState.Loading)
        firestore.collection(Constants.QUIZ_TABLE)
            .document(id)
            .set(quiz)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Quiz Successfully added!"))
                } else {
                    result.invoke(UiState.Failed("Failed adding quiz..."))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun getQuiz(lessonID: String, result: (UiState<List<Quiz>>) -> Unit) {
        val quizList : MutableList<Quiz> = mutableListOf()
        result.invoke(UiState.Loading)
        firestore.collection(Constants.QUIZ_TABLE)
            .whereEqualTo("lessonID",lessonID)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.Failed(it.message!!))
                }
                value?.let {
                    quizList.clear()
                    for (snapshot in it.documents) {
                        val data = snapshot.toObject(Quiz::class.java)
                        if (data != null) {
                            quizList.add(data)
                        }
                    }
                    result.invoke(UiState.Successful(quizList))
                }
            }
    }

    override fun updateQuiz(quizID: String, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteQuiz(quizID: String, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.Loading)
        firestore.collection(Constants.QUIZ_TABLE)
            .document(quizID)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Quiz Deleted Successfully!"))
                } else {
                    result.invoke(UiState.Failed("Failed deleting quiz..."))
                }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun uploadImage(imageUri: Uri, lessonID: String, result: (UiState<Uri>) -> Unit) {
        val storage  = firebaseStorage.getReference(Constants.QUIZ_TABLE)
            .child(lessonID)
            .child(System.currentTimeMillis().toString() + "." + Files.getFileExtension(imageUri.toString()))
        result.invoke(UiState.Loading)
        storage.putFile(imageUri)
            .addOnSuccessListener {
                storage.downloadUrl.addOnSuccessListener { uri1: Uri ->
                    result.invoke(UiState.Successful(uri1))
                }
            }
            .addOnFailureListener{
                result.invoke(UiState.Failed(it.message!!))
            }
    }
}