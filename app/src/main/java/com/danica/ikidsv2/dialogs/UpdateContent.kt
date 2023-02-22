package com.danica.ikidsv2.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentUpdateContentBinding
import com.danica.ikidsv2.models.Content


private const val POSITION = "POSITION"
private const val LESSON_ID = "LESSON_ID"
private const val CONTENT = "CONTENT"

class UpdateContent : DialogFragment() {

    private var position: Int? = null
    private var lessonID: String? = null
    private var content : Content? = null
    private lateinit var binding : FragmentUpdateContentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        arguments?.let {
            position = it.getInt(POSITION)
            lessonID = it.getString(LESSON_ID)
            content = it.getParcelable(CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUpdateContentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        content?.let {
            display(it)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(position: Int,lessonID : String, content: Content) =
            UpdateContent().apply {
                arguments = Bundle().apply {
                    putInt(POSITION, position)
                    putString(LESSON_ID, lessonID)
                    putParcelable(CONTENT,content)
                }
            }
    }
    fun display(content: Content) {
        if (!content.image.isNullOrEmpty()) {
            Glide.with(binding.root.context).load(content.image).into(binding.imageContent)
        }
        binding.editDescIlocano.setText(content.description!![0])
        binding.editDescTagalog.setText(content.description[1])
        binding.editDescEnglish.setText(content.description[2])
    }
}