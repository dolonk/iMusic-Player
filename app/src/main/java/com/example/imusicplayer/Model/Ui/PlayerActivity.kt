package com.example.imusicplayer.Model.Ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.Model.Ui.PlayerActivity.Companion.mediaPlayer
import com.example.imusicplayer.Model.Ui.PlayerActivity.Companion.musicListPa
import com.example.imusicplayer.Model.Ui.PlayerActivity.Companion.songPosition
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
        setTheme(R.style.coolPink)
        setContentView(binding.root)

        initializeData()
        setPlayPauseSongId()
        setPreviousNextSong()
    }

    private fun setPreviousNextSong() {
        binding.pPreviousSongID.setOnClickListener {
            setSongPosition(increment = false)
            setLayout()
            createdMediaPlayer()
        }
        binding.pSongNextID.setOnClickListener {
            setSongPosition(increment = true)
            setLayout()
            createdMediaPlayer()
        }
    }

    private fun setSongPosition(increment: Boolean) {
        if (increment) {
            if (musicListPa.size - 1 == songPosition)
                songPosition = 0
            else ++songPosition
        } else
            if (0 == songPosition)
                songPosition = musicListPa.size - 1
            else --songPosition
    }

    private fun setPlayPauseSongId() {
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

    private fun setLayout() {
        Glide.with(this).load(musicListPa[songPosition].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(binding.pSongPicID)
        binding.pSongTittleID.text = musicListPa[songPosition].title
    }


    private fun initializeData() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicListPa = ArrayList()
                musicListPa.addAll(MainActivity.MusicListMA)
                setLayout()
                createdMediaPlayer()
            }
            "MainActivity"->{
                musicListPa = ArrayList()
                musicListPa.addAll(MainActivity.MusicListMA)
                musicListPa.shuffle()
                setLayout()
                createdMediaPlayer()
            }
        }
    }

    private fun createdMediaPlayer() {
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(musicListPa[songPosition].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            isPlaying = true
            binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
        } catch (e: Exception) {
            return
        }
    }

}
