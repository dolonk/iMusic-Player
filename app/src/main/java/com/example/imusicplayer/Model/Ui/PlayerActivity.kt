package com.example.imusicplayer.Model.Ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.*
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
        var nowPlayingSongId: String = ""
        var isFavourite: Boolean = false
        var fIndex: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Application Back Button
        binding.backBtnID.setOnClickListener { finish() }

        //Auto song increment
        if (intent.data?.scheme.contentEquals("content")) {
            songPosition = 0
            val intentService = Intent(this, MusicService::class.java)
            bindService(intentService, this, BIND_AUTO_CREATE)
            startService(intentService)
            musicListPa = ArrayList()
            musicListPa.add(getMusicDetails(intent.data!!))
            Glide.with(this).load(getImageArt(musicListPa[songPosition].path) )
                .apply(RequestOptions().placeholder(R.drawable.music_icon))
                .into(binding.pSongPicID)
            binding.pSongTittleID.text = musicListPa[songPosition].title
        } else {
            initializeData()
        }
        setPlayPauseSongIdBtn()
        setPreviousNextSongBtn()
        setSeekBar()
        setRepeatSongBtn()
        setEqualizerBtn()
        setTimerBtn()
        setShareBtn()
        setFavouriteBtn()
    }

    private fun getMusicDetails(contentUri: Uri): DomainMusic {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION)
            cursor = this.contentResolver.query(contentUri, projection, null, null, null)
            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path = dataColumn?.let { cursor.getString(it) }
            val duration = durationColumn?.let { cursor.getLong(it) }!!
            return DomainMusic(
                id = "Unknown",
                title = path.toString(),
                album = "Unknown",
                artist = "Unknown",
                duration = duration,
                imageUri = "Unknown",
                path = path.toString()
            )
        } finally {
            cursor?.close()
        }
    }

    private fun setFavouriteBtn() {
        binding.favouriteListID.setOnClickListener {
            if (isFavourite) {
                isFavourite = false
                binding.favouriteListID.setImageResource(R.drawable.empty_favourite_icon)
                FavouriteActivity.favouriteSong.removeAt(fIndex)
            } else {
                isFavourite = true
                binding.favouriteListID.setImageResource(R.drawable.favourite_icon)
                FavouriteActivity.favouriteSong.add(musicListPa[songPosition])
            }
        }
    }

    private fun setShareBtn() {
        binding.shareID.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPa[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File"))
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
                    Toast.makeText(
                        baseContext,
                        "Music will stop after 15 minutes",
                        Toast.LENGTH_LONG
                    ).show()
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
                        binding.timerID.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.icon_color
                            )
                        )
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
        fIndex = favouriteChecker(musicListPa[songPosition].id)

        Glide.with(this).load(musicListPa[songPosition].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(binding.pSongPicID)
        binding.pSongTittleID.isSelected = true
        binding.pSongTittleID.text = musicListPa[songPosition].title

        //Repeat Button
        if (repeat) {
            binding.pRepeatID.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        }

        //Timer Button
        if (min15 || min30 || min60) {
            binding.timerID.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        }

        //Favourite Button
        fIndex = favouriteChecker(musicListPa[songPosition].id)
        if (isFavourite) {
            binding.favouriteListID.setImageResource(R.drawable.favourite_icon)
        } else
            binding.favouriteListID.setImageResource(R.drawable.empty_favourite_icon)

        // Custom layout song design
        val img = getImageArt(musicListPa[songPosition].path)
        val image = if (img != null) {
            BitmapFactory.decodeByteArray(img, 0, img.size)
        } else {
            BitmapFactory.decodeResource(
                resources,
                R.drawable.music_icon
            )
        }
        val bgColor = getMainColor(image)
        val gradient = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(0xFFFFFF, bgColor))
        binding.root.background = gradient
        window?.statusBarColor = bgColor
    }

    private fun initializeData() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "NowPlaying" -> {
                setLayout()
                binding.pSeekBarTimeStartID.text =
                    formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.pSeekBarTimeEndID.text =
                    formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.pSeekBarID.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.pSeekBarID.max = musicService!!.mediaPlayer!!.duration
                if (isPlaying) binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
                else binding.pPlayPauseID.setIconResource(R.drawable.play_icon)
            }
            "PlaylistDetailsShuffle" -> {
                initServiceAndPlaylist(PlayListActivity.refPlaylist.ref[PlaylistDetails.currentPlayListPosition].plyList, shuffle = false)
            }
            "PlaylistDetailsAdapter" -> {
                initServiceAndPlaylist(PlayListActivity.refPlaylist.ref[PlaylistDetails.currentPlayListPosition].plyList, shuffle = true)
            }
            "FavouriteShuffle" -> {
                initServiceAndPlaylist(FavouriteActivity.favouriteSong, shuffle = true)
            }
            "FavouriteAdapter" -> {
                initServiceAndPlaylist(FavouriteActivity.favouriteSong, shuffle = false)
            }
            "MusicAdapterSearch" -> {
                initServiceAndPlaylist(MainActivity.musicListSearch, shuffle = false)
            }
            "MusicAdapter" -> {
                initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = false)
            }
            "MainActivity" -> {
                initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = true)
            }
            "PlayNextActivity"->initServiceAndPlaylist(PlayNextActivity.playNextList, shuffle = false, playNext = true)
        }
        if (musicService!= null && !isPlaying) setPlaySong()
    }

    private fun startService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
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
            nowPlayingSongId = musicListPa[songPosition].id
        } catch (e: Exception) {
            return
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (musicService == null) {
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
            musicService!!.audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(
                musicService,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        createdMediaPlayer()
        musicService!!.setSeekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    // Auto song incriment after the song end
    override fun onCompletion(mp: MediaPlayer?) {
        try {
            setSongPosition(increment = true)
            createdMediaPlayer()
            setLayout()
        } catch (e: Exception){}

        // play_next layout update here
        Glide.with(applicationContext)
            .load(musicListPa[songPosition].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(binding.pSongPicID)
        binding.pSongTittleID.isSelected = true
        binding.pSongTittleID.text = musicListPa[songPosition].title

        //NowPlayingSong layout update for play_next song
        NowPlayingSong.binding.nowPlayingSongTittleID.isSelected = true
        Glide.with(applicationContext).load(musicListPa[songPosition].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(NowPlayingSong.binding.nowPlayingImageID)
        NowPlayingSong.binding.nowPlayingSongTittleID.text = musicListPa[songPosition].title
    }

    // for equalizer feature
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK)
            return
    }

    override fun onDestroy() {
        super.onDestroy()
        if (musicListPa[songPosition].id == "Unknown" && !isPlaying) exitApplication()
    }

    private fun initServiceAndPlaylist(playlist: ArrayList<DomainMusic>, shuffle: Boolean, playNext: Boolean = false){
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        musicListPa = ArrayList()
        musicListPa.addAll(playlist)
        if(shuffle) musicListPa.shuffle()
        setLayout()
        if(!playNext) PlayNextActivity.playNextList = ArrayList()
    }
}