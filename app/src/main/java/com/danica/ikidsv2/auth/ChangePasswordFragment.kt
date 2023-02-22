package com.danica.ikidsv2.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.danica.ikidsv2.databinding.FragmentChangePasswordBinding
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ChangePasswordFragment : Fragment() {


    private lateinit var authService: AuthServiceImpl
    private lateinit var binding : FragmentChangePasswordBinding
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        val auth = FirebaseAuth.getInstance()
        authService = AuthServiceImpl(auth, FirebaseFirestore.getInstance(), FirebaseStorage.getInstance())
        binding.buttonSavePassword.setOnClickListener {
            val old = binding.edtPassword.text.toString()
            val new = binding.edtNewPassword.text.toString()
            val confirm = binding.edtConfirmPassword.text.toString()
            if (old.isEmpty()) {
                binding.inputPassword.error = "This field is required"
            } else if (new.isEmpty()) {
                binding.inputNewPassword.error = "This field is required"
            } else if (confirm.isEmpty()) {
                binding.inputConfirmPassword.error = "This field is required"
            } else if (new != confirm) {
                binding.inputNewPassword.error = "New Password don't match"
            } else {
                auth.currentUser?.let {
                    authenticate(it, it.email!!,old,new)
                }
            }
        }
    }
    private fun authenticate(user : FirebaseUser, email : String, oldPassword : String,newPassword: String) {
        authService.reAuthenticateAccount(user,email, oldPassword) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Loading.....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    changePassword(it.data, newPassword = newPassword)
                }
            }
        }
      
    }
    private fun changePassword(user : FirebaseUser, newPassword : String) {
        authService.changePassword(user,newPassword) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Changing password.....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    if (it.data) {
                        Toast.makeText(binding.root.context,"Password changed successfully",Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}