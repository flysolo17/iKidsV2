package com.danica.ikidsv2.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.ActivityGameBinding
import com.danica.ikidsv2.dialogs.LoseDialog
import com.danica.ikidsv2.dialogs.WinDialog
import com.danica.ikidsv2.models.Choices
import com.danica.ikidsv2.models.Quiz
import com.danica.ikidsv2.service.quiz.QuizServiceImpl
import com.danica.ikidsv2.student.adapter.GameAdapter
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class GameActivity : AppCompatActivity() ,GameAdapter.GameClickListener{
    private lateinit var binding : ActivityGameBinding
    private lateinit var quizService : QuizServiceImpl
    private val loadingDialog = LoadingDialog(this)
    private lateinit var quizList : MutableList<Quiz>
    private var totalCount = 0
    private var currentCount = 0
    private var score = 0
    private var health = 3f
    private var currentAnswer : MutableList<String> = mutableListOf()
    private var lessonID: String ? = null
    private lateinit var quiz: Quiz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ratingHealth.rating = health
        quizList = mutableListOf()
        quizService = QuizServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        lessonID = intent.getStringExtra("lessonID")
        lessonID?.let {
            getQuiz(it)
        }
        binding.textAnswer.addTextChangedListener(answerTextWatcher)
        binding.imageViewBack.setOnClickListener {
            finish()
        }
    }
    override fun onChoiceClicked(choice: String) {
        currentAnswer.add(choice)
        binding.textAnswer.text = currentAnswer.joinToString(separator = " ")
        if (currentAnswer.size == quiz.choices?.size && binding.textAnswer.text.toString() != quiz.answer) {
            currentAnswer.clear()
            binding.textAnswer.text = currentAnswer.joinToString(separator = " ")
            bindRecyclerview(quiz)
            health -= 1
            binding.ratingHealth.rating = health
            if (health == 0f) {
                val loseDialog = LoseDialog()
                if (!loseDialog.isAdded) {
                    loseDialog.show(supportFragmentManager,"Loser")
                }
            }
        }
    }
    private fun getQuiz(id : String) {
        quizList.clear()
        quizService.getQuiz(id) {
            when(it){
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting Quiz")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    quizList.addAll(it.data)
                    totalCount = quizList.size
                    quiz = quizList[currentCount]
                    bindRecyclerview(quiz)
                }
            }
        }
    }
    private val answerTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            try {
                if (s.toString().trim() == quizList[currentCount].answer?.trim()) {
                    score += 10
                    binding.textScore.text = score.toString()
                    if (currentCount < totalCount) {
                        lessonID?.let {
                            val winDialog = WinDialog.newInstance(score,it)
                            if (!winDialog.isAdded) {
                                winDialog.show(supportFragmentManager,"Win")
                            }
                        }
                    } else {
                        currentCount += 1
                        quiz = quizList[currentCount]
                        s.clear()
                        currentAnswer.clear()
                        bindRecyclerview(quiz)
                    }
                }
            } catch (ignored: NumberFormatException) {
                Toast.makeText(this@GameActivity,ignored.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindRecyclerview(quiz: Quiz) {
            dispayGameInfo(quiz)
            val data : MutableList<Choices> = mutableListOf()
            quiz.choices?.map {
                data.add(Choices(it,false))
            }
            binding.recyclerviewChoices.apply {
                layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
                adapter = quiz.choices?.let {
                    GameAdapter(this@GameActivity,
                        data,this@GameActivity)
                }
            }
    }
    private fun dispayGameInfo(quiz: Quiz) {
        if (!quiz.image.isNullOrEmpty()) {
            Glide.with(this).load(quiz.image).into(binding.imageGuess)
        }
        binding.currentCount.text = "${ currentCount + 1 }"
    }
}