package com.example.imusicplayer.Service.Services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.imusicplayer.Model.Ui.NowPlayingSong
import com.example.imusicplayer.Model.Ui.PlayerActivity
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.formatDuration
import com.example.imusicplayer.Service.Domain.getImageArt

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager


    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseBtn: Int) {
        //For Action Notification Intent
        val intent = Intent(baseContext, PlayerActivity::class.java)
        intent.putExtra("index", PlayerActivity.songPosition)
        intent.putExtra("class", "NowPlaying")
        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val previousIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.PREVIOUS)
        val previousPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // for notification image icon
        val imageArt = getImageArt(PlayerActivity.musicListPa[PlayerActivity.songPosition].path)
        val image = if (imageArt != null) {
            BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.music_icon)
        }

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerActivity.musicListPa[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPa[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.notification_music_icon)
            .setLargeIcon(image)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.navigate_previous_back, "Previous", previousPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.navigate_next_icon, "Next", nextPendingIntent)
            .addAction(R.drawable.exit_icon, "Exit", exitPendingIntent)
            .build()


        startForeground(13, notification)
    }

    fun createdMediaPlayer() {
        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null) PlayerActivity.musicService!!.mediaPlayer =
                MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPa[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)

            // for Seekbar process
            PlayerActivity.binding.pSeekBarTimeStartID.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.pSeekBarTimeEndID.text =
                formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.pSeekBarID.progress = 0
            PlayerActivity.binding.pSeekBarID.max = mediaPlayer!!.duration
            PlayerActivity.nowPlayingSongId =
                PlayerActivity.musicListPa[PlayerActivity.songPosition].id
        } catch (e: Exception) {
            return
        }
    }

    fun setSeekBarSetup() {
        runnable = Runnable {
            PlayerActivity.binding.pSeekBarTimeStartID.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.pSeekBarID.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    // Handling audio manager like call and other changes
    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            PlayerActivity.binding.pPlayPauseID.setIconResource(R.drawable.play_icon)
            NowPlayingSong.binding.nowPlayingPlayPauseID.setIconResource(R.drawable.play_icon)
            PlayerActivity.isPlaying = false
            mediaPlayer!!.pause()
            showNotification(R.drawable.play_icon)
        } else {
            //play music
            PlayerActivity.binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
            NowPlayingSong.binding.nowPlayingPlayPauseID.setIconResource(R.drawable.pause_icon)
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
            showNotification(R.drawable.pause_icon)
        }
    }

}