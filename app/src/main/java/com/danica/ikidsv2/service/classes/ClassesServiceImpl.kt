package com.danica.ikidsv2.service.classes

import android.widget.Toast
import com.danica.ikidsv2.models.Classes
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.utils.Constants
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ClassesServiceImpl(val firestore: FirebaseFirestore) : ClassesService {
    override fun addClasses(classes: Classes, result: (UiState<String>) -> Unit) {
        val id : String = firestore.collection(Constants.CLASSES_TABLE).document().id
        classes.id = id
        firestore.collection(Constants.CLASSES_TABLE)
            .document(id)
            .set(classes)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Class Successfully Added!"))
                } else {
                    result.invoke(UiState.Successful("Failed to add Class!"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun deleteClasses(classID: String, result: (UiState<String>) -> Unit) {
        firestore.collection(Constants.CLASSES_TABLE)
            .document(classID)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Class Successfully Deleted!"))
                } else {
                    result.invoke(UiState.Successful("Failed to delete Class!"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun getAllClasses(teacherID: String, result: (UiState<List<Classes>>) -> Unit) {
        val classList  = mutableListOf<Classes>()
        firestore.collection(Constants.CLASSES_TABLE)
            .whereEqualTo("teacherID",teacherID)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.Failed("Failed getting classes!"))
                }
                value?.let {
                    classList.clear()
                    for (snapshot in value.documents) {
                        val classes = snapshot.toObject(Classes::class.java)
                        if (classes != null) {
                            classList.add(classes)
                        }
                    }
                    result.invoke(UiState.Successful(classList))
                }
            }
    }

    override fun getAllClass(result: (UiState<List<Classes>>) -> Unit) {
        val classList  = mutableListOf<Classes>()
        firestore.collection(Constants.CLASSES_TABLE)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.Failed("Failed getting classes!"))
                }
                value?.let {
                    classList.clear()
                    for (snapshot in value.documents) {
                        val classes = snapshot.toObject(Classes::class.java)
                        if (classes != null) {
                            classList.add(classes)
                        }
                    }
                    result.invoke(UiState.Successful(classList))
                }
            }
    }

    override fun getTeacherInfo(teacherID: String, result: (UiState<User>) -> Unit) {
        firestore.collection(Constants.USER_TABLE)
            .document(teacherID)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val data = it.toObject(User::class.java)
                    if (data != null) {
                        result.invoke(UiState.Successful(data))
                    }
                } else {
                    result.invoke(UiState.Failed("Failed getting user!"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun addStudent(classID : String ,studentID: String, result: (UiState<String>) -> Unit) {
        firestore.collection(Constants.CLASSES_TABLE)
            .document(classID)
            .update("students",FieldValue.arrayUnion(studentID))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Successfully joined the class!"))
                } else {
                    result.invoke(UiState.Successful("Failed to join the Class!"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun getClass(studentID: String, result: (UiState<List<Classes>>) -> Unit) {
        val classes : MutableList<Classes> = mutableListOf()
        result.invoke(UiState.Loading)
        firestore.collection(Constants.CLASSES_TABLE)
            .whereArrayContains("students",studentID)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (snapshot in it.result) {
                        val data = snapshot.toObject(Classes::class.java)
                        classes.add(data)
                    }
                    result.invoke(UiState.Successful(classes))
                } else {
                    result.invoke(UiState.Failed("Failed getting classes"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }
}