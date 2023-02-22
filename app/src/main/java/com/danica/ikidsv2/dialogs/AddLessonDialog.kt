package com.danica.ikidsv2.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentAddLessonDialogBinding
import com.danica.ikidsv2.models.Classes
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.service.classes.ClassesServiceImpl
import com.danica.ikidsv2.service.lesson.LessonServiceImpl
import com.danica.ikidsv2.teacherNav.adapters.Classes2Adapter
import com.danica.ikidsv2.teacherNav.adapters.ClassesAdapter
import com.danica.ikidsv2.teacherNav.adapters.LessonsAdapter
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class AddLessonDialog : DialogFragment() ,Classes2Adapter.Classes2AdapterClickListener{

    private lateinit var binding : FragmentAddLessonDialogBinding
    private lateinit var classesService: ClassesServiceImpl
    private val auth = FirebaseAuth.getInstance()
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var classList : MutableList<Classes>
    private var classes = mutableListOf<String>()
    private lateinit var lessonsService : LessonServiceImpl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddLessonDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classesService  = ClassesServiceImpl(FirebaseFirestore.getInstance())
        lessonsService = LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        loadingDialog = LoadingDialog(view.context)
        classList = mutableListOf()
        auth.currentUser?.let {
            getAllClasses(it.uid)
        }
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        binding.createLesson.setOnClickListener {
            val title = binding.edtTitle.text.toString()
            val desc = binding.edtDesc.text.toString()
            if (title.isEmpty()) {
                binding.inputTitle.error = "This field is required!"
            } else if (desc.isEmpty()) {
                binding.inputDesc.error = "This field is required!"
            } else {
                auth.currentUser?.let {
                    val lesson = Lessons("",it.uid,"",title,desc, mutableListOf(),classes,false,System.currentTimeMillis())
                    saveLesson(lesson)
                }
            }
        }
    }
    private  fun saveLesson(lessons: Lessons) {
        lessonsService.createLesson(lessons) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Creating Lessons....")
                }

                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    if (it.data) {
                        Toast.makeText(binding.root.context,"Lesson Created!",Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
        }
    }
    private fun getAllClasses(id : String) {
        classList.clear()
        classesService.getAllClasses(id) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting All Classes....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    binding.recyclerClasses.apply {
                        layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = Classes2Adapter(binding.root.context,it.data,this@AddLessonDialog)
                        addItemDecoration(DividerItemDecoration(binding.root.context, RecyclerView.VERTICAL))
                    }
                    classList.addAll(it.data)
                }
            }
        }
    }
    override fun isCheck(id: String, data: Boolean) {
        if (data) {
            if (!classes.contains(id)) {
                classes.add(id)
            }
        } else {
            classes.remove(id)
        }
    }


}