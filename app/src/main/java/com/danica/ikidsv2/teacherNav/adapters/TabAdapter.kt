package com.danica.ikidsv2.teacherNav.adapters

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.danica.ikidsv2.teacherNav.HomeFragment
import com.danica.ikidsv2.teacherNav.tabs.LessonHomeTab
import com.danica.ikidsv2.teacherNav.tabs.LessonQuizTab
import com.danica.ikidsv2.teacherNav.tabs.LessonSettingsTab
private const val ARG_LESSON_ID = "lessonID"
class TabAdapter(
    @NonNull fragmentManager: FragmentManager?,
    @NonNull lifecycle: Lifecycle?, private val lessonID : String) :
    FragmentStateAdapter(fragmentManager!!, lifecycle!!) {
    @NonNull
    override fun createFragment(position: Int): Fragment {
        val fragment : Fragment = when (position) {
            0 -> {
                LessonHomeTab()
            }
            1 -> {
                LessonQuizTab()
            }
            2 -> {
                LessonSettingsTab()
            }
            else -> {
                LessonHomeTab()
            }
        }
        fragment.arguments = Bundle().apply {
            putString(ARG_LESSON_ID,lessonID)
        }
        return fragment
    }

    override fun getItemCount(): Int {
        return 3
    }
}