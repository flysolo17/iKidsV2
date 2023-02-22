package com.danica.ikidsv2.service.auth

import android.net.Uri
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseUser

interface AuthService {
    fun login(email : String,password : String,result  : (UiState<FirebaseUser>) -> Unit)
    fun signup(email: String,password: String,user : User,result  : (UiState<User>) -> Unit)
    fun createAccount(user : User,result  : (UiState<Boolean>) -> Unit)
    fun getUserInfo(id : String ,result  : (UiState<User>) -> Unit)
    fun reAuthenticateAccount( user : FirebaseUser,email: String,password: String,result: (UiState<FirebaseUser>) -> Unit)
    fun resetPassword(email : String,result: (UiState<String>) -> Unit)
    fun changePassword(user: FirebaseUser,password: String,result: (UiState<Boolean>) -> Unit)
    fun updateUserName(uid : String , name : String,result: (UiState<String>) -> Unit)
    fun logout()
    fun getAllStudent(result: (UiState<List<User>>) -> Unit)
    fun updateAvatar(uid : String,avatar : String ,result: (UiState<String>) -> Unit)
    fun updateInfo(uid: String,name : String,gender : String,result: (UiState<String>) -> Unit)
    fun uploadAvatar(uid: String,uri: Uri,result: (UiState<String>) -> Unit)
    fun updateTeacherInfo(uid: String,name : String,avatar: String, result: (UiState<String>) -> Unit)
}