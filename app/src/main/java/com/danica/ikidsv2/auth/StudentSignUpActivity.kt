package com.danica.ikidsv2.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.ActivityStudentSignUpBinding
import com.danica.ikidsv2.dialogs.SelectNameDialog
import com.danica.ikidsv2.models.Classes
import com.danica.ikidsv2.models.Info
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.models.UserType
import com.danica.ikidsv2.service.auth.AuthService
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.service.classes.ClassesServiceImpl
import com.danica.ikidsv2.student.StudentMainActivity
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.danica.ikidsv2.utils.Validation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class StudentSignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStudentSignUpBinding

    private lateinit var classList : MutableList<Classes>
    private lateinit var classService : ClassesServiceImpl
    private val loadingDialog = LoadingDialog(this)
    private val validation = Validation()
    private lateinit var authService: AuthServiceImpl
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.cardClassInfo.visibility = View.VISIBLE
            binding.textClassName.text = classList.filter { it.code == s.toString() }[0].name
            classList.filter { it.code == s.toString() }[0].teacherID?.let { getTeacherInfo(it) }
        }

        override fun afterTextChanged(s: Editable) {

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val firestore = FirebaseFirestore.getInstance()
        authService = AuthServiceImpl(FirebaseAuth.getInstance(),firestore, FirebaseStorage.getInstance())
        classService = ClassesServiceImpl(firestore)
        classList = mutableListOf()
        getAllClasses()
        binding.autoClassList.addTextChangedListener(textWatcher)
        binding.imageViewBack.setOnClickListener {
            finish()
        }
        binding.buttonSignIn.setOnClickListener {
            finish()
        }
        binding.buttonSignUp.setOnClickListener {
            val code = binding.autoClassList.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            if (code.isEmpty()) {
                binding.inputCode.error ="This field is required!"
            } else if (!validation.validateEmail(binding.inputEmail) || !validation.validatePassword(binding.inputPassword)) {
                return@setOnClickListener
            } else {
                val user = User("",email,UserType.LEARNER,"","","",System.currentTimeMillis())
                signUp(email,password,user,code)
            }
        }
    }
    private fun signUp(email : String,password : String,user: User,code: String) {
        authService.signup(email,password,user) {
            when(it) {
                is UiState.Failed ->{
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Authenticating user!")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    saveUser(user,code)

                }
            }
        }
    }
    private fun saveUser(user: User, code : String) {
        authService.createAccount(user) {
            when(it) {
                is UiState.Failed ->{
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Creating user!")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    if (it.data) {
                        Toast.makeText(this,"Successfully Created",Toast.LENGTH_SHORT).show()
                        val classID = classList.filter { data -> data.code == code }[0].id
                        addToClass(classID!!,user.id!!)
                    }
                }
            }
        }
    }
    private fun addToClass(classID : String,studentID : String) {
        classService.addStudent(classID,studentID) {
            when(it) {
                is UiState.Failed ->{
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Adding to class!")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.data,Toast.LENGTH_SHORT).show()
                    val selectNameDialog = SelectNameDialog()
                    if (!selectNameDialog.isAdded) {
                        selectNameDialog.show(supportFragmentManager,"select name")
                    }
                }
            }
        }
    }
    private fun getAllClasses(){
        classList.clear()
        classService.getAllClass {
            when(it) {
                is UiState.Failed ->{
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting classes!")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    classList.addAll(it.data)
                    val classCodeAdapter = ArrayAdapter(
                        this,
                        R.layout.list_class_codes,
                        it.data.map { data -> data.code }
                    )
                    binding.autoClassList.setAdapter(classCodeAdapter)
                }
            }
        }
    }
    private fun getTeacherInfo(teacherID : String) {
        classService.getTeacherInfo(teacherID) {
            when(it) {
                is UiState.Failed ->{
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    binding.textClassTeacher.text = "Loading....."
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    binding.textClassTeacher.text = it.data.name
                    if (!it.data.avatar.isNullOrEmpty()) {
                        Glide.with(this).load(it.data.avatar).into(binding.userProfile)
                    }
                }
            }
        }
    }

}