package com.danica.ikidsv2.student

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.ActivityStudentMainBinding
import com.danica.ikidsv2.dialogs.ExitDialog
import com.danica.ikidsv2.dialogs.WelcomeDialog
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class StudentMainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStudentMainBinding
    private val authService = AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())
    private val loadingDialog = LoadingDialog(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLearn.setOnClickListener {
            startActivity(Intent(this,LearnActivity::class.java))
        }
        binding.buttonPlay.setOnClickListener {
            startActivity(Intent(this,PlayActivity::class.java))

        }
        binding.cardProfile.setOnClickListener {
            startActivity(Intent(this,UpdateProfileActivity::class.java))

//            FirebaseAuth.getInstance().signOut()
//            finish()
        }
        binding.buttonQuit.setOnClickListener {
            val exitDialog = ExitDialog()
            if (!exitDialog.isAdded) {
                exitDialog.show(supportFragmentManager,"Exit dialog")
            }
        }
    }
    private fun getUserInfo(id : String) {
        authService.getUserInfo(id) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()

                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Updating Profile info!")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    displayInfo(it.data)
                }
            }
        }
    }
    private fun displayInfo(data: User) {
        if (!data.avatar.isNullOrEmpty()) {
            Glide.with(this).load(data.avatar).into(binding.imageUserProfile)
        }
        binding.textFullname.text = data.name
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            getUserInfo(currentUser.uid)
        }
        val welcomeDialog = WelcomeDialog()
        if (!welcomeDialog.isAdded) {
            welcomeDialog.show(supportFragmentManager,"Welcome")
        }
    }
}