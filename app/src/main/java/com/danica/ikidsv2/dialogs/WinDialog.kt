package com.danica.ikidsv2.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.danica.ikidsv2.databinding.WinDialogBinding
import com.danica.ikidsv2.models.Score
import com.danica.ikidsv2.service.score.ScoreServiceImpl
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


private const val ARG_SCORE= "SCORE"
private const val ARG_LESSON_ID= "LESSON_ID"

class WinDialog : DialogFragment() {
    private var score: Int? = null
    private var lessonID : String? = null
    private lateinit var binding : WinDialogBinding
    private lateinit var scoreService: ScoreServiceImpl
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        arguments?.let {
            score = it.getInt(ARG_SCORE)
            lessonID = it.getString(ARG_LESSON_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = WinDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        scoreService = ScoreServiceImpl(FirebaseFirestore.getInstance())
        val auth = FirebaseAuth.getInstance()
        score?.let {
            binding.textYourScore.text = it.toString()
        }
        binding.buttonNext.setOnClickListener {
            auth.currentUser?.let {
                val scores = Score("",lessonID,it.uid, score = score,System.currentTimeMillis())
                saveScore(scores)
            }
        }
        binding.buttonHome.setOnClickListener {
            auth.currentUser?.let {
                val scores = Score("",lessonID,it.uid, score = score,System.currentTimeMillis())
                saveScore(scores)
            }
        }
        binding.buttonRetry.setOnClickListener {
            dismiss().also {
                activity?.finish()
            }
        }
    }
    private fun saveScore(score : Score) {
        scoreService.addScore(score) {
            when(it){
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Saving Score....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    dismiss()
                    activity?.finish()
                }
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(score: Int,lessonID : String) =
            WinDialog().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SCORE, score)
                    putString(ARG_LESSON_ID,lessonID)
                }
            }
    }
}