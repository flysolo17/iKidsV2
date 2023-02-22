package com.danica.ikidsv2.teacherNav.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentLessonHomeTabBinding
import com.danica.ikidsv2.dialogs.AddLessonContentDialog
import com.danica.ikidsv2.dialogs.UpdateContent
import com.danica.ikidsv2.models.Content
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.service.lesson.LessonServiceImpl
import com.danica.ikidsv2.teacherNav.adapters.ContentAdapter
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


private const val ARG_LESSON_ID = "lessonID"


class LessonHomeTab : Fragment() ,ContentAdapter.ContentClickListener{
    private var lessonID: String? = null
    private lateinit var binding : FragmentLessonHomeTabBinding
    private lateinit var lessonService : LessonServiceImpl
    private lateinit var loadingDialog : LoadingDialog
    private lateinit var contentList : MutableList<Content>
    private lateinit var contentAdapter: ContentAdapter
    private val  auth = FirebaseAuth.getInstance()
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
        binding = FragmentLessonHomeTabBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentList = mutableListOf()
        lessonService = LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())

        loadingDialog = LoadingDialog(view.context)
        binding.buttonAddContent.setOnClickListener {
            lessonID?.let {
                val addLessonContentDialog = AddLessonContentDialog.newInstance(it)
                if (!addLessonContentDialog.isAdded) {
                    addLessonContentDialog.show(childFragmentManager,"Add Lesson Content")
                }
            }
        }
        contentAdapter  = ContentAdapter(view.context,contentList,this)
        binding.recyclerviewContents.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = contentAdapter
        }
        lessonID?.let {
            getLesson(it)
        }
    }
    private fun getLesson(id : String) {
        lessonService.getLessonByID(id) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_LONG).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting lesson...")
                }
                is UiState.Successful -> {
                    contentList.clear()
                    loadingDialog.stopLoading()
                    it.data.content?.let { list ->
                        contentList.addAll(list)
                        contentAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun deleteContent(position: Int) {
        contentList.removeAt(position)
        lessonID?.let {
            MaterialAlertDialogBuilder(binding.root.context)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this content?")
                .setPositiveButton("Yes") { dialog,_ ->
                    deleteContent(it,contentList,position)
                }
                .setNegativeButton("No") { dialog ,_->
                    dialog.dismiss()

                }
                .show()
        }

    }
    private fun deleteContent(lessonID : String,content: List<Content>,position: Int) {
        lessonService.deleteContent(lessonID,content) { state ->
            when(state) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,state.message, Toast.LENGTH_LONG).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting lesson...")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,state.data,Toast.LENGTH_LONG).show()
                    contentAdapter.notifyItemRemoved(position)
                }
            }
        }
    }
    override fun editContent(position: Int) {
        lessonID?.let {
            val updateContent = UpdateContent.newInstance(position,it, contentList[position])
            if (!updateContent.isAdded) {
                updateContent.show(childFragmentManager,"Update Content")
            }
        }
    }

}