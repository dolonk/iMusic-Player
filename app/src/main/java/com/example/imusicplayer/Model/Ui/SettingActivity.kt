package com.example.imusicplayer.Model.Ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imusicplayer.R
import com.example.imusicplayer.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Setting"

    }
}