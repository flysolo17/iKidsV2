package com.danica.ikidsv2.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentUpdateTeacherProfileBinding
import com.danica.ikidsv2.service.auth.AuthService
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException


class UpdateTeacherProfile : Fragment() {
    private lateinit var binding : FragmentUpdateTeacherProfileBinding
    private val args by navArgs<UpdateTeacherProfileArgs>()
    private var imageUri : Uri? = null
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var authService: AuthServiceImpl
    private val auth = FirebaseAuth.getInstance()
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentUpdateTeacherProfileBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        authService  = AuthServiceImpl(auth, FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        args.user?.let {
            binding.inputName.setText(it.name)
            if (!it.avatar.isNullOrEmpty()) {
                Glide.with(view.context).load(it.avatar).into(binding.userProfile)
            }
        }
        binding.buttonChangeProfile.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val data = result.data
                try {
                    if (data?.data != null) {
                        data.data?.let {
                            imageUri = it
                            binding.userProfile.setImageURI(imageUri)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        binding.buttonSave.setOnClickListener {
            val name = binding.inputName.text.toString()
            if (name.isEmpty()) {
                binding.layoutName.error = "This field is required!"
            } else {
                args.user?.let {
                    if (imageUri != null) {
                        uploadProfile(name, it.id!!, imageUri!!)
                    } else {
                        saveData(it.id!!,name,it.avatar!!)
                    }
                }
            }
        }
    }

    private fun uploadProfile(name : String,uid : String ,uri : Uri) {
        authService.uploadAvatar(uid,uri) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Uploading profile....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    saveData(uid,name,it.data)
                }
            }
        }
    }
    private fun saveData(uid: String ,name : String , avatar : String) {
        authService.updateTeacherInfo(uid,name,avatar) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Saving info....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }
}