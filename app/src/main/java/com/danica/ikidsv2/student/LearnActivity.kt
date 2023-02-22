package com.danica.ikidsv2.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.ActivityLearnBinding
import com.danica.ikidsv2.dialogs.StudentLessonDialog
import com.danica.ikidsv2.models.Content
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.service.auth.AuthService
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.service.classes.ClassesServiceImpl
import com.danica.ikidsv2.service.lesson.LessonServiceImpl
import com.danica.ikidsv2.teacherNav.adapters.LessonsAdapter
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LearnActivity : AppCompatActivity(),LessonsAdapter.LessonAdapterClicked {
    private lateinit var binding : ActivityLearnBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var classesService: ClassesServiceImpl
    private lateinit var authService: AuthServiceImpl
    private lateinit var lessonService: LessonServiceImpl
    private val loadingDialog = LoadingDialog(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authService = AuthServiceImpl(auth, FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        classesService = ClassesServiceImpl(FirebaseFirestore.getInstance())
        lessonService = LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        auth.currentUser?.let {
            getALlMyClasses(it.uid)
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
                    binding.textClassName.text = it.data[0].name

                    if (it.data.isNotEmpty()) {
                        it.data[0].teacherID?.let { it1 -> getClassInfo(it1,it.data[0].id!!) }
                    }
                }
            }
        }
    }
    private fun getClassInfo(id : String,classId: String) {
        authService.getUserInfo(id) {
            when(it) {
                is UiState.Failed ->{
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting teacher info")
                    binding.textTeacherName.text = "Loading..."

                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    binding.textTeacherName.text = it.data.name
                    if (!it.data.avatar.isNullOrEmpty()) {
                        Glide.with(binding.root.context).load(it.data.avatar)
                    }
                    getLessons(classId)
                }
            }
        }
    }
    private fun getLessons(classId : String) {
        lessonService.getAllLessonsByClass(classId) {
            when(it) {
                is UiState.Failed ->{
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting lessons..")


                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    binding.recyclerviewLearn.apply {
                        layoutManager = LinearLayoutManager(this@LearnActivity)
                        adapter = LessonsAdapter(this@LearnActivity,it.data,this@LearnActivity)
                    }
                }
            }
        }
    }

    override fun onLessonClicked(lesson: Lessons) {
        val array = arrayListOf<Content>()
        array.addAll(lesson.content!!)
        val studentContentDialog = StudentLessonDialog.newInstance(array,lesson.title!!,lesson.description!!)
        if (!studentContentDialog.isAdded) {
            studentContentDialog.show(supportFragmentManager, "Lesson Contents")
        }
    }
}