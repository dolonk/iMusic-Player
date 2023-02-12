package com.example.imusicplayer.Model.Ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imusicplayer.R
import com.example.imusicplayer.databinding.ActivityFeadbackBinding

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeadbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeadbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Feedback"
    }
}