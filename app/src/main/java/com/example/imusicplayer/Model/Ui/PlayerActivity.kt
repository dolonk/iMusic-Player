package com.example.imusicplayer.Model.Ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.Service.MusicService
import com.example.imusicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity(), ServiceConnection {
    private lateinit var binding: ActivityPlayerBinding

    companion object {
        lateinit var musicListPa: ArrayList<DomainMusic>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setTheme(R.style.coolPink)
        setContentView(binding.root)

        //For Start Services
        val intent = Intent(this, MusicService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)

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
       musicService!!.mediaPlayer!!.pause()
    }

    private fun setPlaySong() {
        binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
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
            }
            "MainActivity" -> {
                musicListPa = ArrayList()
                musicListPa.addAll(MainActivity.MusicListMA)
                musicListPa.shuffle()
                setLayout()
            }
        }
    }

    private fun createdMediaPlayer() {
        try {
            if ( musicService!!.mediaPlayer == null)  musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPa[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
        } catch (e: Exception) {
            return
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createdMediaPlayer()
        musicService!!.showNotification()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
     musicService = null
    }

}
