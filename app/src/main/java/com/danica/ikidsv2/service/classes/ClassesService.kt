package com.danica.ikidsv2.service.classes

import com.danica.ikidsv2.models.Classes
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.utils.UiState

interface ClassesService {
    fun addClasses(classes : Classes,result : (UiState<String>) -> Unit)
    fun deleteClasses(classID : String,result : (UiState<String>) -> Unit)
    fun getAllClasses(teacherID : String,result: (UiState<List<Classes>>) -> Unit)
    fun getAllClass(result: (UiState<List<Classes>>) -> Unit)
    fun getTeacherInfo(teacherID: String,result: (UiState<User>) -> Unit)
    fun addStudent(classID : String ,studentID : String,result: (UiState<String>) -> Unit)
    fun getClass(studentID: String,result: (UiState<List<Classes>>) -> Unit)

}