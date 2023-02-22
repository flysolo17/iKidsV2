package com.danica.ikidsv2.teacherNav.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentMyStudentBinding
import com.danica.ikidsv2.models.Leaderboard
import com.danica.ikidsv2.models.Score
import com.danica.ikidsv2.service.classes.ClassesServiceImpl
import com.danica.ikidsv2.teacherNav.adapters.MyStudentAdapter
import com.danica.ikidsv2.utils.Constants
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyStudentFragment : Fragment() {

    private lateinit var binding : FragmentMyStudentBinding
    private lateinit var classesService : ClassesServiceImpl
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val studentsList : MutableList<String> = mutableListOf()
    private val leaderboardList = mutableListOf<Leaderboard>()
    private lateinit var myStudentAdapter: MyStudentAdapter
    private  var scores : ArrayList<Score> ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scores  = it.getParcelableArrayList(Constants.SCORES_TABLE)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyStudentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        loadingDialog  = LoadingDialog(view.context)
        classesService = ClassesServiceImpl(firestore)
        auth.currentUser?.let {
            getAllClasses(it.uid)
        }

    }
    private fun getAllClasses(myID : String) {
        studentsList.clear()
        classesService.getAllClasses(myID) {
            when(it) {
                is UiState.Failed ->{
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Getting Classes")

                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    it.data.map { classes ->
                        classes.students?.map { id ->
                            if (!studentsList.contains(id)) {
                                studentsList.add(id)
                            }
                        }
                    }
                    setupRecyclerview(studentsList)
                }
            }
        }
    }
    private fun setupRecyclerview(list : List<String>) {
        leaderboardList.clear()
        list.map { stud->
            val score = scores!!.filter { s -> s.studentID == stud }
            val leaderboard = Leaderboard(stud,score.sumOf { s -> s.score!! })
            leaderboardList.add(leaderboard)
        }
        myStudentAdapter = MyStudentAdapter(binding.root.context,leaderboardList.sortedByDescending { it.score })
        binding.recyclerviewMyStudents.apply {
            layoutManager = LinearLayoutManager(binding.root.context)
            adapter = myStudentAdapter
        }
    }

}