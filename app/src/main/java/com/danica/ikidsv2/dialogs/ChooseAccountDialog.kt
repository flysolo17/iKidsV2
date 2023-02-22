package com.danica.ikidsv2.dialogs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.danica.ikidsv2.R
import com.danica.ikidsv2.auth.StudentSignUpActivity
import com.danica.ikidsv2.auth.TeacherSignUpActivity
import com.danica.ikidsv2.databinding.ChooseAccountDialogBinding
import com.danica.ikidsv2.models.UserType


class ChooseAccountDialog : DialogFragment() {

    private lateinit var binding: ChooseAccountDialogBinding
    private var SELECTED_USER : Int ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ChooseAccountDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardLearner.setOnClickListener {
            selectUser(0)
        }
        binding.cardTeacher.setOnClickListener {
            selectUser(1)
        }
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        binding.imageViewNext.setOnClickListener {
            SELECTED_USER?.let {
                if (it == 0) {
                    startActivity(Intent(activity,StudentSignUpActivity::class.java))
                    dismiss()
                } else {
                    startActivity(Intent(activity,TeacherSignUpActivity::class.java))
                    dismiss()
                }
                return@setOnClickListener
            }
            Toast.makeText(view.context,"Select user type first",Toast.LENGTH_SHORT).show()
        }
    }
    private fun selectUser(user : Int) {
        if (user == 0) {
            SELECTED_USER = 0
            binding.checkLearner.visibility = View.VISIBLE
            binding.checkTeacher.visibility = View.GONE
        } else {
            binding.checkTeacher.visibility = View.VISIBLE
            binding.checkLearner.visibility = View.GONE
            SELECTED_USER = 1
        }
    }
}