package com.danica.ikidsv2.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentStudentLessonDialogBinding
import com.danica.ikidsv2.models.Content
import com.danica.ikidsv2.student.adapter.StudentContentAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val ARGS_CONTENTS = "contents"

private const val ARGS_TITLE= "TITLE"

private const val ARGS_DESC = "DESC"
class StudentLessonDialog : DialogFragment() {
    private var contents: ArrayList<Content>? = null
    private var title : String ? = null
    private var desc : String? = null
    private lateinit var binding : FragmentStudentLessonDialogBinding
    private var language = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        arguments?.let {
            contents = it.getParcelableArrayList(ARGS_CONTENTS)
            title = it.getString(ARGS_TITLE)
            desc = it.getString(ARGS_DESC)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStudentLessonDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textTitle.text =title ?: "No Title"
        binding.textDesc.text = desc ?:"No Description"
        binding.buttonDismiss.setOnClickListener {
            dismiss()
        }
        updateRecyclerview(language)
        binding.cardLanguage.setOnClickListener {
            val items = arrayOf("Ilocano", "Tagalog", "English")
            MaterialAlertDialogBuilder(view.context)
                .setTitle("Select Language")
                .setItems(items) { dialog, which ->
                    binding.textLanguage.text = items[which]
                    language = which
                    updateRecyclerview(language)
                }
                .show()
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(content: ArrayList<Content>,title : String, desc : String) =
            StudentLessonDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARGS_CONTENTS, content)
                    putString(ARGS_TITLE,title)
                    putString(ARGS_DESC,desc)
                }
            }
    }
    private fun updateRecyclerview(language : Int) {
        binding.recyclerviewContents.apply {
            layoutManager = LinearLayoutManager(binding.root.context)
            adapter = StudentContentAdapter(binding.root.context,contents!!.toList(),language)
        }
    }
}