package com.danica.ikidsv2.teacherNav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentViewLessonBinding
import com.danica.ikidsv2.teacherNav.adapters.TabAdapter
import com.google.android.material.tabs.TabLayout


class ViewLessonFragment : Fragment() {
    private lateinit var binding : FragmentViewLessonBinding
    private val args by navArgs<ViewLessonFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentViewLessonBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.lessonID?.let {
            setupTabLayout(it)

        }
    }
    private fun setupTabLayout(lessonID : String) {
        val tabAdapter = TabAdapter(childFragmentManager,lifecycle,lessonID)
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