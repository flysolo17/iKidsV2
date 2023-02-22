package com.danica.ikidsv2.teacherNav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentLessonsBinding
import com.danica.ikidsv2.dialogs.AddLessonDialog
import com.danica.ikidsv2.models.Content
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.service.lesson.LessonServiceImpl
import com.danica.ikidsv2.teacherNav.adapters.LessonsAdapter
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LessonsFragment : Fragment(),LessonsAdapter.LessonAdapterClicked {

    private lateinit var binding : FragmentLessonsBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var lessonsService : LessonServiceImpl
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLessonsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(binding.root.context)
        lessonsService = LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        currentUser?.let { user ->
            getAllLesson(user.uid)
        }
        binding.fabAddLesson.setOnClickListener {
            val addLessonDialog = AddLessonDialog()
            if (!addLessonDialog.isAdded) {
                addLessonDialog.show(childFragmentManager,"Add Lesson")
            }
        }

    }

    private fun getAllLesson(id : String){
        lessonsService.getAllLessons(id) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_LONG).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting all lessons....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    binding.recyclerviewLessons.apply {
                        layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = LessonsAdapter(binding.root.context,it.data,this@LessonsFragment)
                        addItemDecoration(DividerItemDecoration(binding.root.context, RecyclerView.VERTICAL))
                    }
                }
            }
        }
    }

    override fun onLessonClicked(lesson: Lessons) {
        val directions = LessonsFragmentDirections.actionNavLessonsToViewLessonFragment(lesson.id)
        findNavController().navigate(directions)
    }

}