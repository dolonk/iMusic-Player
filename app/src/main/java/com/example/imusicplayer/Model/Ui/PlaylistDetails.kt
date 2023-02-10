package com.example.imusicplayer.Model.Ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imusicplayer.R
import com.example.imusicplayer.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetails : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDetailsBinding

    companion object {
        var currentPlayListPosition = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlayListPosition = intent.extras?.getInt("index") as Int
    }
}