package com.danica.ikidsv2.service.lesson

import android.net.Uri
import com.danica.ikidsv2.models.Content
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.utils.Constants
import com.danica.ikidsv2.utils.UiState
import com.google.common.io.Files
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.checkerframework.checker.guieffect.qual.UI

class LessonServiceImpl(val firestore: FirebaseFirestore,val firebaseStorage: FirebaseStorage) : LessonService {
    override  fun createLesson(lesson: Lessons, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        val id = firestore.collection(Constants.LESSONS_TABLE).document().id
        lesson.id = id
        firestore.collection(Constants.LESSONS_TABLE)
            .document(id)
            .set(lesson)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful(true))
                } else {
                    result.invoke(UiState.Failed("Failed creating lesson"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override  fun deleteLesson(id: String, result: (UiState<Boolean>) -> Unit) {
        firestore.collection(Constants.LESSONS_TABLE)
            .document(id)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful(true))
                } else {
                    result.invoke(UiState.Failed("Failed to delete"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override  fun getAllLessons(
        teacherID: String,
        result: (UiState<List<Lessons>>) -> Unit
    ) {
        val list : MutableList<Lessons> = mutableListOf()
        firestore.collection(Constants.LESSONS_TABLE)
            .whereEqualTo("teacherID",teacherID)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.Failed(it.message!!))
                }
                value?.let {
                    list.clear()
                    for (snapshot in it.documents) {
                        val data = snapshot.toObject(Lessons::class.java)
                        if (data != null) {
                            list.add(data)
                        }
                    }
                    result.invoke(UiState.Successful(list))
                }
            }
    }

    override fun getAllLessonsByClass(classID: String, result: (UiState<List<Lessons>>) -> Unit) {
        val list : MutableList<Lessons> = mutableListOf()
        firestore.collection(Constants.LESSONS_TABLE)
            .whereArrayContains("classes",classID)
            .orderBy("createdAt",Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.Failed(it.message!!))
                }
                value?.let {
                    list.clear()
                    for (snapshot in it.documents) {
                        val data = snapshot.toObject(Lessons::class.java)
                        if (data != null) {
                            list.add(data)
                        }
                    }
                    result.invoke(UiState.Successful(list))
                }
            }
    }

    override fun getLessonByID(lessonID: String, result: (UiState<Lessons>) -> Unit) {
        firestore.collection(Constants.LESSONS_TABLE)
            .document(lessonID)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.Failed(it.message!!))
                }
                value?.let {
                    if (it.exists()) {
                        val data = it.toObject(Lessons::class.java)
                        if (data != null) {
                            result.invoke(UiState.Successful(data))
                        }
                    }
                }
            }
    }

    override fun updateLesson(
        id : String,
        title: String,
        desc: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(Constants.LESSONS_TABLE)
            .document(id)
            .update("title",title,"description",desc)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful(true))
                } else {
                    result.invoke(UiState.Failed("Edit Failed"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override  fun uploadImage(imageUri: Uri, lessonID: String, result: (UiState<Uri>) -> Unit) {
        val storage  = firebaseStorage.getReference(Constants.LESSONS_TABLE)
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

    override fun updateImage(imageLink : String,lessonID: String, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        firestore.collection(Constants.LESSONS_TABLE)
            .document(lessonID)
            .update("image",imageLink)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful(true))
                } else {
                    result.invoke(UiState.Failed("Failed to update image"))
                }
            }
            .addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun updateAvailability(
        lessonID: String,
        isOpen: Boolean,

    ) {
        firestore.collection(Constants.LESSONS_TABLE)
            .document(lessonID)
            .update("isOpen",isOpen)
    }

    override fun addLessonContent(
        lessonID: String,
        content: Content,
        result: (UiState<Boolean>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(Constants.LESSONS_TABLE)
            .document(lessonID)
            .update("content",FieldValue.arrayUnion(content))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful(true))
                } else {
                    result.invoke(UiState.Successful(false))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun deleteContent(
        lessonID: String,
        contents: List<Content>,
        result: (UiState<String>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(Constants.LESSONS_TABLE)
            .document(lessonID)
            .update("content",contents)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Successfully deleted!"))
                } else {
                    result.invoke(UiState.Failed("Failed to delete content"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }


}