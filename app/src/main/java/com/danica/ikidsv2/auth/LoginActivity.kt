package com.danica.ikidsv2.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.danica.ikidsv2.MainActivity
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.ActivityLoginBinding
import com.danica.ikidsv2.dialogs.ChooseAccountDialog
import com.danica.ikidsv2.models.UserType
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.student.StudentMainActivity
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.danica.ikidsv2.utils.Validation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authService = AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())
    private val loadingDialog = LoadingDialog(this)
    private val validation = Validation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonSignUp.setOnClickListener {
            val chooseDialog = ChooseAccountDialog()
            if (!chooseDialog.isAdded) {
                chooseDialog.show(supportFragmentManager,"Choose User")
            }
        }

        binding.buttonForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
        binding.buttonLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            if (!validation.validateEmail(binding.inputEmail) || !validation.validatePassword(binding.inputPassword)) {
                return@setOnClickListener
            }
            authService.login(email,password) {
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
                        updateUI(it.data.uid)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            updateUI(user.uid)
        }
    }


    private fun updateUI(userID : String) {
        authService.getUserInfo(userID) {
            when (it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting User...")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    if (it.data.type == UserType.TEACHER) {
                        startActivity(Intent(this,MainActivity::class.java))
                    } else {
                        startActivity(Intent(this,StudentMainActivity::class.java))
                    }
                }
            }
        }
    }
    private fun showForgotPasswordDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_forgot_password,null,false)
        val email = view.findViewById<EditText>(R.id.inputEmail)
        MaterialAlertDialogBuilder(this)
            .setTitle("Forgot Password")
            .setView(view)
            .setPositiveButton("send") { dialog,_ ->
                val textEmail = email.text.toString()
                if (textEmail.isNotEmpty()) {
                    sendResetLink(textEmail)
                } else {
                    email.error = "Invalid email!"
                }
            }.setNegativeButton("cancel") { dialog,_ ->
                dialog.dismiss()
            }.show()
    }
    private fun sendResetLink(email : String) {
        authService.resetPassword(email) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Sending reset link...")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.data,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}