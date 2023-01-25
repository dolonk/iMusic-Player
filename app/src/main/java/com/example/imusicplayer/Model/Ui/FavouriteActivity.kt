package com.example.imusicplayer.Model.Ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imusicplayer.R

class FavouriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        setContentView(R.layout.activity_favourite)
    }
}