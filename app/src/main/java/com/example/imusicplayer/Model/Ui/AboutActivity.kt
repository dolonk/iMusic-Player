package com.example.imusicplayer.Model.Ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imusicplayer.R
import com.example.imusicplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "About"
        binding.aboutTextId.text = aboutText()
    }

    private fun aboutText(): String {
        return "Developer By: Dolon Km" +
                "\n\n If you want to provide feedback, I will be grateful for this "
    }
}