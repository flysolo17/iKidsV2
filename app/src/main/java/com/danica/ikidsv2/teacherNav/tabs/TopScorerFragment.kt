package com.danica.ikidsv2.teacherNav.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentTopScorerBinding
import com.danica.ikidsv2.models.Score
import com.danica.ikidsv2.models.TopScores
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.service.score.ScoreServiceImpl
import com.danica.ikidsv2.teacherNav.adapters.LeaderBoardAdapter
import com.danica.ikidsv2.utils.Constants
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class TopScorerFragment : Fragment() {
    private lateinit var binding :FragmentTopScorerBinding
    private val authService = AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())
    private var scores: ArrayList<Score> ? = null
    private val topScoreList = mutableListOf<TopScores>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scores = it.getParcelableArrayList(Constants.SCORES_TABLE)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTopScorerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loadingDialog = LoadingDialog(view.context)
        authService.getAllStudent {
           when(it) {
               is UiState.Failed -> {
                   loadingDialog.stopLoading()
                   Toast.makeText(view.context,it.message, Toast.LENGTH_SHORT).show()
               }
               UiState.Loading ->{
                   loadingDialog.showLoadingDialog("Getting students")
               }
               is UiState.Successful -> {
                   loadingDialog.stopLoading()
                   topScoreList.clear()
                   it.data.map { stud ->
                       val yourScore= scores!!.filter { data -> data.studentID == stud.id }
                       val topScore = TopScores(stud.avatar,stud.name,yourScore.sumOf { s-> s.score!! })
                       topScoreList.add(topScore)
                   }
                   binding.recyclerviewScores.apply {
                       layoutManager = LinearLayoutManager(view.context)
                       adapter = LeaderBoardAdapter(view.context,topScoreList.sortedByDescending { s -> s.score })
                   }
               }
           }
       }
    }

}