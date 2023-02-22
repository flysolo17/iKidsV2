package com.danica.ikidsv2.teacherNav.tabs

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentLessonSettingsTabBinding
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.service.lesson.LessonService
import com.danica.ikidsv2.service.lesson.LessonServiceImpl
import com.danica.ikidsv2.teacherNav.ViewLessonFragmentArgs
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.danica.ikidsv2.utils.dateFormat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_LESSON_ID = "lessonID"

class LessonSettingsTab : Fragment() {
    private lateinit var binding : FragmentLessonSettingsTabBinding

    private lateinit var lessonService: LessonServiceImpl
    private lateinit var loadingDialog: LoadingDialog
    private var lessonID: String? = null

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lessonID = it.getString(ARG_LESSON_ID)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLessonSettingsTabBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        lessonService = LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        lessonID?.let {
            getLesson(it)
        }
        //pick image from gallery
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val data = result.data
                try {
                    if (data?.data != null) {
                        data.data?.let {
                            changeImage(it,lessonID!!)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            lessonID?.let {
                lessonService.updateAvailability(it,isChecked)
            }
        }
        binding.buttonEdit.setOnClickListener {
            if (binding.layoutEdit.isVisible) {
                binding.layoutEdit.visibility = View.GONE
            } else {
                binding.layoutEdit.visibility = View.VISIBLE
            }
        }
        binding.buttonSaveEdit.setOnClickListener {
            val title = binding.edtTitle.text.toString()
            val desc = binding.edtDesc.text.toString()
            if (title.isEmpty()) {
                binding.inputTitle.error = "This field is required"
            } else if (desc.isEmpty()) {
                binding.inputDesc.error = "This field is required"
            } else {
                lessonID?.let {
                    saveEdit(it,title,desc)
                }
            }
        }
        binding.buttonDelete.setOnClickListener {
            MaterialAlertDialogBuilder(view.context)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this lesson")
                .setPositiveButton("Yes") {dialog,_ ->
                    lessonID?.let {
                        delete(it)
                        dialog.dismiss()
                    }
                }.setNegativeButton("Cancel") { dialog,_ ->
                    dialog.dismiss()
                }.show()

        }
        binding.buttonChangeImage.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }

    }

    private fun saveEdit(id : String ,title : String ,desc : String) {
            lessonService.updateLesson(id,title,desc) {
                when(it) {
                    is UiState.Failed -> {
                        loadingDialog.stopLoading()
                        Toast.makeText(binding.root.context,it.message,Toast.LENGTH_LONG).show()
                    }
                    is UiState.Loading -> {
                        loadingDialog.showLoadingDialog("Updating lesson")
                    }
                    is UiState.Successful -> {
                        loadingDialog.stopLoading()
                        if (it.data) {
                            Toast.makeText(binding.root.context,"Updated Successfully",Toast.LENGTH_LONG).show()
                            binding.layoutEdit.visibility = View.GONE
                        }
                    }
                }
        }
    }
    private fun delete(id : String) {
        lessonService.deleteLesson(id) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_LONG).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Deleting lesson")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    if (it.data) {
                        Toast.makeText(binding.root.context," Successfully Deleted!",Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
    private fun getLesson(id : String) {
        lessonService.getLessonByID(id) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_LONG).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting lesson...")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    displayLesson(it.data)
                }
            }
        }
    }
    private fun changeImage(imageURI : Uri, lessonID : String){
            lessonService.uploadImage(imageURI,lessonID) {
                when(it) {
                    is UiState.Failed -> {
                        loadingDialog.stopLoading()
                        Toast.makeText(binding.root.context,it.message,Toast.LENGTH_LONG).show()
                    }
                    UiState.Loading -> {
                        loadingDialog.showLoadingDialog("Uploading image...")
                    }
                    is UiState.Successful -> {
                        loadingDialog.stopLoading()
                        updateImage(it.data.toString(),lessonID)
                    }
                }
            }

    }
    private fun updateImage(imageURI: String,lessonID: String) {
        lessonService.updateImage(imageURI,lessonID) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_LONG).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Updating lesson")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    if (it.data) {
                        Toast.makeText(binding.root.context," Successfully Updated!",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    private fun displayLesson(lessons: Lessons) {
        if (lessons.image!!.isNotEmpty()) {
            Glide.with(binding.root.context).load(lessons.image).into(binding.lessonImage)
        }
        binding.switch1.isChecked = lessons.isOpen
        binding.textTitle.text = lessons.title
        binding.textDesc.text = lessons.description
        binding.textDate.text= dateFormat(lessons.createdAt!!)
        binding.edtTitle.setText(lessons.title)
        binding.edtDesc.setText(lessons.description)
        if (lessons.isOpen) {
            binding.buttonIsOpen.text = "OPEN"
            binding.buttonIsOpen.setTextColor(Color.GREEN)
        } else {
            binding.buttonIsOpen.text = "CLOSED"
            binding.buttonIsOpen.setTextColor(Color.RED)
        }

    }
}