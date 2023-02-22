package com.danica.ikidsv2

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.danica.ikidsv2.auth.LoginActivity
import com.danica.ikidsv2.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val path = "android.resource://$packageName/${R.raw.ikids}"
        val uri = Uri.parse(path)
        binding.viewVideo.setVideoURI(uri)
        binding.viewVideo.setOnPreparedListener { mp: MediaPlayer ->
            mp.start()
            binding.image.visibility = View.GONE
        }


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 6000)
    }
}