package com.danica.ikidsv2.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.EditProfileDialogBinding
import com.danica.ikidsv2.models.Gender
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

private const val ARG_NAME = "name"
private const val ARG_ID = "ID"
private const val ARG_GENDER = "gender"


class EditProfileDialog : DialogFragment() {
    private var id: String? = null
    private var name: String? = null
    private var gender: String? = null
    private var SELECTED_GENDER : Int ? = null
    private val authService = AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())
    private lateinit var binding : EditProfileDialogBinding
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Light_NoTitleBar_Fullscreen
        )
        arguments?.let {
            id = it.getString(ARG_ID)
            name = it.getString(ARG_NAME)
            gender = it.getString(ARG_GENDER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = EditProfileDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(view.context)
        SELECTED_GENDER = if (gender == Gender.MALE.toString()) 0 else if (gender == Gender.FEMALE.toString()) 1 else null
        SELECTED_GENDER?.let {
            selectUser(it)
        }
        binding.inputName.setText(name!!)
        binding.imageBoy.setOnClickListener {
            selectUser(0)
        }
        binding.imageGirl.setOnClickListener {
            selectUser(1)
        }
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        binding.buttonSave.setOnClickListener {
            val name = binding.inputName.text.toString()
            val gender : String = if (SELECTED_GENDER == 0) Gender.MALE.toString() else Gender.FEMALE.toString()
            if (name.isEmpty()) {
                binding.layoutName.error = "this field is required!"
            } else if (gender.isEmpty()) {
                Toast.makeText(view.context,"Please select your gender",Toast.LENGTH_SHORT).show()
            } else {
                id?.let {
                    updateInfo(it,name,gender)
                }
            }
        }
    }
    private fun updateInfo(uid : String,name : String,gender: String) {
        authService.updateInfo(uid,name,gender) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                is UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Updating Profile info!")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }
    private fun selectUser(user : Int) {
        if (user == 0) {
            SELECTED_GENDER = 0
            binding.checkBoy.visibility = View.VISIBLE
            binding.checkGirl.visibility = View.GONE
        } else {
            SELECTED_GENDER = 1
            binding.checkGirl.visibility = View.VISIBLE
            binding.checkBoy.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String, name: String,gender : String) =
            EditProfileDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, id)
                    putString(ARG_NAME, name)
                    putString(ARG_GENDER, gender)
                }
            }
    }
}