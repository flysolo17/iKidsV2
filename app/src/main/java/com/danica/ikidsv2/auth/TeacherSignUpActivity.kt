package com.danica.ikidsv2.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.danica.ikidsv2.MainActivity
import com.danica.ikidsv2.databinding.ActivityTeacherSignUpBinding
import com.danica.ikidsv2.models.Info
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.models.UserType
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.danica.ikidsv2.utils.Validation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class TeacherSignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTeacherSignUpBinding
    private  val authService = AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())
    private lateinit var loadingDialog: LoadingDialog
    private val validation = Validation()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(binding.root.context)
        binding.imageViewBack.setOnClickListener {
            finish()
        }
        binding.buttonSignUp.setOnClickListener {
            val fullname = binding.edtFullname.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            if (fullname.isEmpty()) {
                binding.inputFullname.error = "This field is required!"
                return@setOnClickListener
            } else if (!validation.validateEmail(binding.inputEmail) || !validation.validatePassword(binding.inputPassword)) {
                return@setOnClickListener
            }
            val user = User("",email,UserType.TEACHER,fullname,"","",System.currentTimeMillis())
            authService.signup(user.email!!,password,user) {
                when(it) {
                    is UiState.Failed -> {
                        loadingDialog.stopLoading()
                        Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Loading -> {
                        loadingDialog.showLoadingDialog("Loading...")
                    }
                    is UiState.Successful -> {
                        loadingDialog.stopLoading()
                        saveAccount(it.data)
                    }
                }
            }
        }
    }
    private fun saveAccount(user: User) {
        authService.createAccount(user) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Saving info.....")
                }
                is UiState.Successful -> {
                    if (it.data) {
                        Toast.makeText(binding.root.context,"Saved Successful!",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                }
            }

        }
    }

}