package com.danica.ikidsv2.dialogs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentViewLessonDialogBinding
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.student.GameActivity

private const val ARG_POSITION = "POSITION"
private const val ARG_LESSON = "LESSON"


class ViewLessonDialog : DialogFragment() {

    private var position: Int? = null
    private var lesson: Lessons? = null
    private lateinit var binding : FragmentViewLessonDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            lesson = it.getParcelable(ARG_LESSON)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewLessonDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageViewBack.setOnClickListener {
            dismiss()
        }
        lesson?.let {
            if (!it.image.isNullOrEmpty()) {
                Glide.with(view.context).load(it.image).into(binding.imageLessonImage)
            }
            binding.textLessonTitle.text = it.title
        }
        position?.let {
            binding.textPosition.text = "Level ${it + 1}"
        }
        binding.imageViewPlay.setOnClickListener {
            lesson?.let {
                startActivity(Intent(activity,GameActivity::class.java).putExtra("lessonID",it.id))
                dismiss()
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(position : Int, lesson: Lessons) =
            ViewLessonDialog().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                    putParcelable(ARG_LESSON, lesson)
                }
            }
    }
}