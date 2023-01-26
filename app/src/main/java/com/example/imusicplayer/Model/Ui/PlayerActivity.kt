package com.example.imusicplayer.Model.Ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    companion object {
        lateinit var musicListPa: ArrayList<DomainMusic>
        var songPosition: Int = 0
        var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setTheme(R.style.coolPinkNav)
        setContentView(binding.root)

        startSong()
        initializeLayout()
        setPlayPauseSong()
    }

    private fun setPlayPauseSong() {
        binding.pPlayPauseID.setOnClickListener {
            if (isPlaying) setPauseSong()
            else setPlaySong()
        }
    }

    private fun setPauseSong() {
        binding.pPlayPauseID.setIconResource(R.drawable.play_icon)
        isPlaying = false
        mediaPlayer!!.pause()
    }

    private fun setPlaySong() {
        binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
        isPlaying = true
        mediaPlayer!!.start()
    }

    private fun initializeLayout() {
        Glide.with(this).load(musicListPa[songPosition].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(binding.pSongPicID)
        binding.pSongTittleID.text = musicListPa[songPosition].title
    }

    private fun startSong() {
        try {
            songPosition = intent.getIntExtra("index", 0)
            when (intent.getStringExtra("class")) {
                "MusicAdapter" -> {
                    musicListPa = ArrayList()
                    musicListPa.addAll(MainActivity.MusicListMA)

                    if (mediaPlayer == null) mediaPlayer = MediaPlayer()
                    mediaPlayer!!.reset()
                    mediaPlayer!!.setDataSource(musicListPa[songPosition].path)
                    mediaPlayer!!.prepare()
                    mediaPlayer!!.start()
                    isPlaying = true
                    binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
                }
            }
        } catch (e: Exception) {
            return
        }
    }
}