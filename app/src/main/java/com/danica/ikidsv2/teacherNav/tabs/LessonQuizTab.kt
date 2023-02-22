package com.danica.ikidsv2.teacherNav.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentLessonQuizTabBinding
import com.danica.ikidsv2.dialogs.AddQuizDialog
import com.danica.ikidsv2.service.quiz.QuizServiceImpl
import com.danica.ikidsv2.teacherNav.adapters.QuizAdapter
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


private const val ARG_LESSON_ID = "lessonID"



class LessonQuizTab : Fragment() {
    private var lessonID: String? = null
    private lateinit var binding : FragmentLessonQuizTabBinding
    private lateinit var quizService : QuizServiceImpl
    private lateinit var loadingDialog: LoadingDialog
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
        binding = FragmentLessonQuizTabBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        quizService = QuizServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        binding.buttonAddQuiz.setOnClickListener {
            lessonID?.let {
                val addQuizDialog = AddQuizDialog.newInstance(it)
                if (!addQuizDialog.isAdded) {
                    addQuizDialog.show(childFragmentManager,"Add Quiz")
                }
            }
        }
        lessonID?.let {
            getAllQuiz(it)
        }
    }
    private fun getAllQuiz(lessonID : String) {
        quizService.getQuiz(lessonID) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting all quizzes")

                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    binding.recyclerviewQuiz.apply {
                        layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = QuizAdapter(binding.root.context,it.data)
                    }
                }
            }
        }
    }

}