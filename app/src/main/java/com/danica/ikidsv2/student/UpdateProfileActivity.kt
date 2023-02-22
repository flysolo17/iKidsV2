package com.danica.ikidsv2.student

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.danica.ikidsv2.auth.LoginActivity
import com.danica.ikidsv2.databinding.ActivityUpdateProfileBinding
import com.danica.ikidsv2.dialogs.EditProfileDialog
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.student.adapter.AvatarAdapter
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class UpdateProfileActivity : AppCompatActivity() ,AvatarAdapter.AvatarClickListener{
    private lateinit var binding : ActivityUpdateProfileBinding
    private val storage = FirebaseStorage.getInstance()
    private lateinit var imagelist : MutableList<String>
    val auth = FirebaseAuth.getInstance()
    private val reference : StorageReference = storage.reference.child("avatars")
    private var user : User ? = null
    val loadingDialog = LoadingDialog(this)
    private val authService = AuthServiceImpl(auth, FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonBack.setOnClickListener {
            finish()
        }

        auth.currentUser?.let {
            getMyProfile(it.uid)
        }
        binding.buttonEditProfile.setOnClickListener {
            user?.let {
                val editProfileDialog = EditProfileDialog.newInstance(it.id!!,it.name!!,it.gender!!)
                if (!editProfileDialog.isAdded) {
                    editProfileDialog.show(supportFragmentManager,"Edit Profile")
                }
            }
        }
        binding.buttonLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") {dialog,_ ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                    dialog.dismiss()
                }
                .setNegativeButton("No") {dialog,_ ->
                    dialog.dismiss()
                }
                .show()

        }
    }
    private fun getAllAvatars(currentAvatar : String) {
        imagelist = mutableListOf()
        reference.listAll()
            .addOnSuccessListener { listResult ->
            imagelist.clear()
            for (file in listResult.items) {
                file.downloadUrl.addOnSuccessListener { uri ->
                    imagelist.add(uri.toString())

                }.addOnSuccessListener {
                    binding.recyclerviewAvatars.apply {
                        layoutManager =GridLayoutManager(this@UpdateProfileActivity,3)
                        adapter = AvatarAdapter( this@UpdateProfileActivity,imagelist,currentAvatar,this@UpdateProfileActivity)
                    }
                }
            }
        }
    }
    private fun getMyProfile(uid : String) {
        authService.getUserInfo(uid) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting profile....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    user = it.data
                    displayInfo(it.data)
                    getAllAvatars(it.data.avatar.toString())
                }
            }
        }
    }

    private fun displayInfo(data: User) {
        if (!data.avatar.isNullOrEmpty()) {
            Glide.with(this).load(data.avatar).into(binding.imageStudentAvatar)
        }
        binding.textStudentName.text = data.name
        binding.textGender.text = if (data.gender.isNullOrEmpty()) "N/A" else data.gender
    }

    override fun onAvatarClicked(url: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Save")
            .setMessage("Are you sure you want to change your avatar ?")
            .setPositiveButton("Save") {dialog ,_ ->
                auth.currentUser?.let {
                    updateAvatar(it.uid,url)
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") {dialog ,_ ->
                dialog.dismiss()
            }.show()
    }
    private fun updateAvatar(uid : String,avatar : String) {
        authService.updateAvatar(uid,avatar) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Updating profile....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()

                    Toast.makeText(this,it.data,Toast.LENGTH_SHORT).show()
                    Glide.with(this).load(avatar).into(binding.imageStudentAvatar)
                }
            }
        }
    }
}