package com.danica.ikidsv2.teacherNav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentHomeBinding
import com.danica.ikidsv2.models.Score
import com.danica.ikidsv2.service.score.ScoreServiceImpl
import com.danica.ikidsv2.teacherNav.adapters.HomeTabAdapter
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private val scoreService = ScoreServiceImpl(FirebaseFirestore.getInstance())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loadingDialog = LoadingDialog(view.context)
        scoreService.getAllScore {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(view.context,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.Loading ->{
                    loadingDialog.showLoadingDialog("Getting scores")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    val scores = arrayListOf<Score>()
                    scores.addAll(it.data)
                    setupTabLayout(scores)
                }
            }
        }

    }
    private fun setupTabLayout(array : ArrayList<Score>) {
        val tabAdapter = HomeTabAdapter(childFragmentManager,lifecycle,array)
        binding.viewpager.adapter = tabAdapter
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewpager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)!!.select()
            }
        })
    }

}