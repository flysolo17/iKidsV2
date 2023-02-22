package com.danica.ikidsv2.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.ActivityPlayBinding
import com.danica.ikidsv2.dialogs.ViewLessonDialog
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.service.classes.ClassesServiceImpl
import com.danica.ikidsv2.service.lesson.LessonServiceImpl
import com.danica.ikidsv2.student.adapter.PlayAdapter
import com.danica.ikidsv2.teacherNav.adapters.LessonsAdapter
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PlayActivity : AppCompatActivity(),PlayAdapter.OnPlayClickListener {
    private lateinit var binding : ActivityPlayBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var classesService: ClassesServiceImpl
    private lateinit var authService: AuthServiceImpl
    private lateinit var lessonService: LessonServiceImpl
    private val loadingDialog = LoadingDialog(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authService = AuthServiceImpl(auth, FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        classesService = ClassesServiceImpl(FirebaseFirestore.getInstance())
        lessonService = LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        auth.currentUser?.let {
            getALlMyClasses(it.uid)
        }
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }
    private fun getALlMyClasses(studentID : String) {
        classesService.getClass(studentID) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting Classes")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    if (it.data.isNotEmpty()) {
                        getLessons(it.data[0].id!!)
                    }
                }
            }
        }
    }
//    private fun getClassInfo(id : String,classId: String) {
//        authService.getUserInfo(id) {
//            when(it) {
//                is UiState.Failed ->{
//                    loadingDialog.stopLoading()
//                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
//                }
//                is UiState.Loading -> {
//                    loadingDialog.showLoadingDialog("Getting teacher info")
//                }
//                is UiState.Successful -> {
//                    loadingDialog.stopLoading()
//                    getLessons(classId)
//                }
//            }
//        }
//    }
    private fun getLessons(classId : String) {
        lessonService.getAllLessonsByClass(classId) {
            when(it) {
                is UiState.Failed ->{
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting lessons..")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    binding.recyclerviewPlay.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = PlayAdapter(context,it.data,this@PlayActivity)
                    }
                }
            }
        }
    }

    override fun onPlay(position: Int, lessons: Lessons) {
        val viewLessonDialog = ViewLessonDialog.newInstance(position,lessons)
        if (!viewLessonDialog.isAdded) {
            viewLessonDialog.show(supportFragmentManager,"ViewLesson")
        }
    }
}