package com.example.imusicplayer.Model.Ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.Service.Domain.exitApplication
import com.example.imusicplayer.Service.Domain.formatDuration
import com.example.imusicplayer.Service.Domain.setSongPosition
import com.example.imusicplayer.Service.Services.MusicService
import com.example.imusicplayer.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {


    companion object {
        lateinit var musicListPa: ArrayList<DomainMusic>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setTheme(R.style.coolPink)
        setContentView(binding.root)
        // Application Back Button
        binding.backBtnID.setOnClickListener { finish() }

        //For Start Services
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)

        initializeData()
        setPlayPauseSongIdBtn()
        setPreviousNextSongBtn()
        setSeekBar()
        setRepeatSongBtn()
        setEqualizerBtn()
        setTimerBtn()
        setShareBtn()
    }

    private fun setShareBtn() {
       binding.shareID.setOnClickListener {
           val shareIntent = Intent()
           shareIntent.action = Intent.ACTION_SEND
           shareIntent.type = "audio/*"
           shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPa[songPosition].path))
           startActivity(Intent.createChooser(shareIntent,"Sharing Music File"))
       }
    }

    private fun setTimerBtn() {
        binding.timerID.setOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer) {
                //show bottom sheet dialog
                val dialog = BottomSheetDialog(this@PlayerActivity)
                dialog.setContentView(R.layout.bottom_sheed_dialog)
                dialog.show()

                dialog.findViewById<LinearLayout>(R.id.minutes15Id)?.setOnClickListener {
                    Toast.makeText(baseContext, "Music will stop after 15 minutes", Toast.LENGTH_LONG).show()
                    binding.timerID.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
                    min15 = true
                    Thread {
                        Thread.sleep(15 * 60000)
                        if (min15) {
                            exitApplication()
                        }
                    }.start()
                    dialog.dismiss()
                }
                dialog.findViewById<LinearLayout>(R.id.minutes30Id)?.setOnClickListener {
                    Toast.makeText(
                        baseContext,
                        "Music will stop after 30 minutes",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.timerID.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
                    min30 = true
                    Thread {
                        Thread.sleep(30 * 60000)
                        if (min30) {
                            exitApplication()
                        }
                    }.start()
                    dialog.dismiss()
                }
                dialog.findViewById<LinearLayout>(R.id.minutes60Id)?.setOnClickListener {
                    Toast.makeText(
                        baseContext,
                        "Music will stop after 60 minutes",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.timerID.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
                    min60 = true
                    Thread {
                        Thread.sleep(60 * 60000)
                        if (min60) {
                            exitApplication()
                        }
                    }.start()
                    dialog.dismiss()
                }
            } else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to stop timer?")
                    .setPositiveButton("Yes") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.timerID.setColorFilter(ContextCompat.getColor(this, R.color.icon_color))
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }
    }

    private fun setEqualizerBtn() {
        binding.equalizeID.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 13)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Equalizer Feature not Supported!\n IS support TO Android Q version Above  ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setRepeatSongBtn() {
        binding.pRepeatID.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.pRepeatID.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            } else {
                repeat = false
                binding.pRepeatID.setColorFilter(ContextCompat.getColor(this, R.color.icon_color))
            }
        }
    }

    private fun setSeekBar() {
        binding.pSeekBarID.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService!!.mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
    }

    private fun setPreviousNextSongBtn() {
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

    private fun setPlayPauseSongIdBtn() {
        binding.pPlayPauseID.setOnClickListener {
            if (isPlaying) setPauseSong()
            else setPlaySong()
        }
    }

    private fun setPauseSong() {
        binding.pPlayPauseID.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun setPlaySong() {
        binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun setLayout() {
        Glide.with(this).load(musicListPa[songPosition].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(binding.pSongPicID)
        binding.pSongTittleID.text = musicListPa[songPosition].title
        if (repeat) {
            binding.pRepeatID.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        }
        if (min15 || min30 || min60) {
            binding.timerID.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        }
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
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPa[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon)

            // for Seekbar process
            binding.pSeekBarTimeStartID.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.pSeekBarTimeEndID.text =
                formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.pSeekBarID.progress = 0
            binding.pSeekBarID.max = musicService!!.mediaPlayer!!.duration

            // Auto song incriment after the song end
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        } catch (e: Exception) {
            return
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createdMediaPlayer()
        musicService!!.setSeekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    // Auto song incriment after the song end
    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createdMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }

    // for equalizer feature
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK)
            return
    }

}
