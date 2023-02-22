package com.danica.ikidsv2.teacherNav.adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.danica.ikidsv2.models.Score
import com.danica.ikidsv2.teacherNav.tabs.MyStudentFragment
import com.danica.ikidsv2.teacherNav.tabs.TopScorerFragment
import com.danica.ikidsv2.utils.Constants

class HomeTabAdapter(fragmentManager: FragmentManager?, lifecycle: Lifecycle?,val array : ArrayList<Score>) : FragmentStateAdapter(
    fragmentManager!!,
    lifecycle!!
) {
    override fun getItemCount(): Int {
        return  2
    }

    override fun createFragment(position: Int): Fragment {
        val frag: Fragment = when(position) {
            0 -> {
                MyStudentFragment()
            }
            1 -> {
                TopScorerFragment()
            }
            else -> {
                MyStudentFragment()
            }
        }
        frag.arguments = Bundle().apply {
            putParcelableArrayList(Constants.SCORES_TABLE,array)
        }
        return frag
    }
}