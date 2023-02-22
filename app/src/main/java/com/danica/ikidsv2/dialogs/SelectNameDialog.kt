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
import com.danica.ikidsv2.databinding.FragmentSelectNameDialogBinding
import com.danica.ikidsv2.service.auth.AuthService
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.student.StudentMainActivity
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class SelectNameDialog : DialogFragment() {
    private lateinit var binding : FragmentSelectNameDialogBinding
    private lateinit var authService: AuthServiceImpl
    private lateinit var auth : FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelectNameDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Light_NoTitleBar_Fullscreen)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        loadingDialog = LoadingDialog(view.context)
        authService = AuthServiceImpl(auth, FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        binding.imageViewBack.setOnClickListener {
            startActivity(Intent(activity,StudentMainActivity::class.java))
            dismiss()
        }
        binding.buttonNext.setOnClickListener {
            val name = binding.edtName.text.toString()
            if (name.isEmpty()) {
                binding.inputName.error = "This field is required!"
            } else {
                auth.currentUser?.let {
                    updateName(it.uid,name)
                }
            }
        }
    }
    private fun updateName(id : String,name : String) {
        authService.updateUserName(id,name) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Updating name")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}