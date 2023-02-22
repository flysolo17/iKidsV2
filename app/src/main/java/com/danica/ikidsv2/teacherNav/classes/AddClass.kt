package com.danica.ikidsv2.teacherNav.classes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentAddClassBinding
import com.danica.ikidsv2.models.Classes
import com.danica.ikidsv2.service.classes.ClassesService
import com.danica.ikidsv2.service.classes.ClassesServiceImpl
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AddClass : Fragment() {
    private lateinit var binding : FragmentAddClassBinding
    private  val auth = FirebaseAuth.getInstance()
    private lateinit var classesService: ClassesServiceImpl
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddClassBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        classesService = ClassesServiceImpl(FirebaseFirestore.getInstance())
        binding.buttonCreateClass.setOnClickListener {
            val name = binding.edtClassName.text.toString()
            val code = binding.edtClassCode.text.toString()
            if (name.isEmpty()) {
                binding.inputClassname.error = "This field is required!"
            } else if (code.isEmpty()) {
                binding.inputClasscCde.error = "This field is required!"
            } else if (code.length != 5) {
                binding.inputClasscCde.error = "Class code should be 5 characters"
            } else {
                auth.currentUser?.let {
                    val classes = Classes("",name, code, listOf(),it.uid,System.currentTimeMillis())
                    saveClass(classes)
                    return@setOnClickListener
                }
                Toast.makeText(view.context,"User not Found!",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveClass(classes : Classes) {
        classesService.addClasses(classes) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Adding Class....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

}