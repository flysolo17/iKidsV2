package com.danica.ikidsv2.teacherNav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentAccountBinding
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class AccountFragment : Fragment() {
    private val authService = AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())
    private lateinit var binding : FragmentAccountBinding
    private lateinit var loadingDialog: LoadingDialog
    private var user : User ? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        FirebaseAuth.getInstance().currentUser?.let { it ->
            authService.getUserInfo(it.uid) { state->
                when(state) {
                    is UiState.Failed -> {
                        loadingDialog.stopLoading()
                        Toast.makeText(view.context,state.message,Toast.LENGTH_SHORT).show()
                    }
                    UiState.Loading -> {
                       loadingDialog.showLoadingDialog("Getting user info....")
                    }
                    is UiState.Successful ->{
                        loadingDialog.stopLoading()
                        user = state.data
                        displayInfo(user =state.data)
                    }
                }
            }
        }
        binding.buttonChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_changePasswordFragment)
        }
        binding.buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.finish()
        }
        binding.buttonEdit.setOnClickListener {
            user?.let {
                val directions = AccountFragmentDirections.actionNavSettingsToUpdateTeacherProfile(it)
                findNavController().navigate(directions)
            }

        }
    }
    private fun displayInfo(user : User) {
        if (!user.avatar.isNullOrEmpty()) {
            Glide.with(this).load(user.avatar).into(binding.userProfile)
        }
        binding.textFullname.text = user.name ?: "No name"
        binding.textEmail.text = user.email ?: "No email"
        binding.textAccountType.text = user.type.toString()
    }
}