package com.danica.ikidsv2

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.bumptech.glide.Glide
import com.danica.ikidsv2.databinding.ActivityMainBinding
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var mAppBarConfiguration: AppBarConfiguration? = null
    private var toolbar: Toolbar? = null
    private val authService = AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val header : View = binding.navigationView.getHeaderView(0)
        val userProfile = header.findViewById<ImageView>(R.id.userProfile)
        val textFullname = header.findViewById<TextView>(R.id.textFullname)
        val textAccountType = header.findViewById<TextView>(R.id.textAccountType)
        FirebaseAuth.getInstance().currentUser?.let { it ->
            authService.getUserInfo(it.uid) { state->
                when(state) {
                    is UiState.Failed -> {
                        textFullname.text = "Error"
                        textAccountType.text = "Error"
                        Toast.makeText(this,state.message,Toast.LENGTH_SHORT).show()
                    }
                    UiState.Loading -> {
                        textFullname.text = "Loading..."
                        textAccountType.text = "Loading.."
                    }
                    is UiState.Successful ->{
                        textFullname.text = state.data.name
                        textAccountType.text = state.data.type.toString()
                        if (!state.data.avatar.isNullOrEmpty()) {
                            Glide.with(this).load(state.data.avatar).into(userProfile)
                        }
                     }
                }
            }
        }


        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_home,
            R.id.nav_classes,
            R.id.nav_lessons,
            R.id.nav_settings,
            R.id.nav_logout
        )
            .setOpenableLayout(binding.drawer)
            .build()

        val navController = findNavController(this, R.id.nav_host_fragment_container)
        setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        setupWithNavController(binding.navigationView, navController)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment_container)
        return (navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }
}