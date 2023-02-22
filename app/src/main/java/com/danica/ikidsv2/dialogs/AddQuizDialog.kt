package com.danica.ikidsv2.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentAddQuizDialogBinding
import com.danica.ikidsv2.models.Content
import com.danica.ikidsv2.models.Quiz
import com.danica.ikidsv2.service.quiz.QuizService
import com.danica.ikidsv2.service.quiz.QuizServiceImpl
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException


private const val ARG_LESSON_ID = "lessonID"



class AddQuizDialog : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var lessonID: String? = null
    private val choiceList = mutableListOf<String>()
    private var imageUri : Uri? = null
    private lateinit var binding : FragmentAddQuizDialogBinding
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var quizService: QuizServiceImpl
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NORMAL,
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
        binding = FragmentAddQuizDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        quizService = QuizServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
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
        binding.buttonAddImage.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        binding.buttonAddChoice.setOnClickListener {
            if (binding.layoutChoicesContianer.isVisible) {
                binding.layoutChoicesContianer.visibility =View.GONE
                return@setOnClickListener
            }
            binding.layoutChoicesContianer.visibility =View.VISIBLE
        }
        binding.buttonCheck.setOnClickListener {
            val choice = binding.edtChoice.text.toString()
            if (choice.isEmpty()) {
                binding.inputChoice.error = "This field is required!"
                return@setOnClickListener
            }
            addChoice(choice)
        }
        binding.buttonSave.setOnClickListener {
            val question = binding.edtQuestion.text.toString()
            val answer = binding.edtAnswer.text.toString()
            if (choiceList.size == 0) {
                Toast.makeText(view.context,"Please add choices",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (question.isEmpty()) {
                binding.inputQuestion.error = " This field is required!"
            } else if (answer.isEmpty()) {
                binding.inputAnswer.error = "This field is required!"
            } else {
                imageUri?.let {
                    val quiz = Quiz("",lessonID!!,"",question,answer,choiceList,System.currentTimeMillis())
                    uploadImage(it ,lessonID!!,quiz)
                    return@setOnClickListener
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(lessonID: String) =
            AddQuizDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_LESSON_ID, lessonID)
                }
            }
    }
    private fun uploadImage(uri: Uri,lessonID: String,quiz: Quiz) {
        quizService.uploadImage(uri,lessonID)
        {
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
                    quiz.image = it.data.toString()
                    saveQuiz(quiz)
                }
            }
        }
    }
    private fun saveQuiz(quiz: Quiz) {
        quizService.addQuiz(quiz) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Saving quiz.....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }
    private fun addChoice(choice : String) {
        binding.layoutChoicesContianer.visibility = View.GONE
        val view = LayoutInflater.from(binding.root.context).inflate(R.layout.layout_choices,binding.layoutChoices,false)
        view.findViewById<TextView>(R.id.textChoice).text = choice
        view.findViewById<ImageButton>(R.id.buttonDeleteChoice).setOnClickListener {
            choiceList.remove(choice)
            binding.layoutChoices.removeView(view)
        }
        choiceList.add(choice)
        binding.layoutChoices.addView(view)
    }
}