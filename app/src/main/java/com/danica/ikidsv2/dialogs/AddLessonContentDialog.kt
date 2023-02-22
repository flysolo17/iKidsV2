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
import androidx.fragment.app.DialogFragment
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentAddLessonContentDialogBinding
import com.danica.ikidsv2.databinding.FragmentAddLessonDialogBinding
import com.danica.ikidsv2.models.Content
import com.danica.ikidsv2.service.lesson.LessonServiceImpl
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException


private const val ARG_LESSON_ID = "LESSON_ID"



class AddLessonContentDialog : DialogFragment() {
    private var lessonID: String? = null
    private var imageUri : Uri ? = null
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var lessonService : LessonServiceImpl
    private lateinit var binding : FragmentAddLessonContentDialogBinding
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        arguments?.let {
            lessonID = it.getString(ARG_LESSON_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddLessonContentDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        lessonService = LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        //pick image from gallery
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val data = result.data
                try {
                    if (data?.data != null) {
                        data.data?.let {
                            imageUri= it
                            binding.imageLesson.setImageURI(imageUri)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

         binding.buttonSave.setOnClickListener {
             val ilocano = binding.editDescIlocano.text.toString()
             val tagalog = binding.editDescTagalog.text.toString()
             val english = binding.editDescEnglish.text.toString()
             if (ilocano.isEmpty()) {
                 binding.inputDescIlocano.error = "This field is required!"
             } else if (tagalog.isEmpty()) {
                 binding.inputDescTagalog.error = "This field is required!"
             } else if (english.isEmpty()) {
                 binding.inputDescEnglish.error = "This field is required!"
             } else {
                 if (lessonID != null) {
                     imageUri?.let {
                         uploadImage(it, lessonID!!, listOf(ilocano,tagalog,english))
                         return@setOnClickListener
                     }
                     Toast.makeText(view.context,"Please add image",Toast.LENGTH_SHORT).show()
                 }
             }
         }
        binding.buttonAddImage.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(lessonID: String) =
            AddLessonContentDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_LESSON_ID, lessonID)
                }
            }
    }
    private fun uploadImage(imageUri: Uri, lessonID: String,description : List<String>) {
        lessonService.uploadImage(imageUri,lessonID) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_LONG).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Uploading image...")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    val content = Content(it.data.toString(),description)
                    saveContent(lessonID,content)
                }
            }
        }
    }
    private fun saveContent(lessonID: String,content : Content) {
        lessonService.addLessonContent(lessonID,content) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_LONG).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Saving content...")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    if (it.data) {
                        Toast.makeText(binding.root.context,"Succefully Added!",Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
        }
    }
}