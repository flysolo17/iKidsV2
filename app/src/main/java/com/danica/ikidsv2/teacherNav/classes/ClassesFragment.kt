package com.danica.ikidsv2.teacherNav.classes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.danica.ikidsv2.R
import com.danica.ikidsv2.databinding.FragmentClassesBinding
import com.danica.ikidsv2.service.classes.ClassesServiceImpl
import com.danica.ikidsv2.teacherNav.adapters.ClassesAdapter
import com.danica.ikidsv2.utils.LoadingDialog
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ClassesFragment : Fragment() ,ClassesAdapter.ClassesClickListener{

    private lateinit var binding : FragmentClassesBinding
    private lateinit var classesService: ClassesServiceImpl
    private val auth = FirebaseAuth.getInstance()
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentClassesBinding.inflate(inflater,container ,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(view.context)
        classesService = ClassesServiceImpl(FirebaseFirestore.getInstance())
        auth.currentUser?.let {
            getAllClasses(it.uid)
        }
        binding.fabAddClass.setOnClickListener {
            findNavController().navigate(R.id.action_nav_classes_to_addClass)
        }
    }

   private fun getAllClasses(id : String) {
       classesService.getAllClasses(id) {
           when(it) {
               is UiState.Failed -> {
                   loadingDialog.stopLoading()
                   Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
               }
               UiState.Loading -> {
                   loadingDialog.showLoadingDialog("Getting All Classes....")
               }
               is UiState.Successful -> {
                   loadingDialog.stopLoading()
                   binding.recycleviewClasses.apply {
                       layoutManager = LinearLayoutManager(binding.root.context)
                       adapter = ClassesAdapter(binding.root.context,it.data,this@ClassesFragment)
                   }
               }
           }
       }
   }

    override fun deleteClass(classID: String) {
        classesService.deleteClasses(classID) {
            when(it) {
                is UiState.Failed -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.Loading -> {
                    loadingDialog.showLoadingDialog("Deleting Class....")
                }
                is UiState.Successful -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}