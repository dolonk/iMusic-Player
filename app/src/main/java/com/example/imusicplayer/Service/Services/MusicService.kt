package com.example.imusicplayer.Service.Services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.imusicplayer.Model.Ui.PlayerActivity
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.getImageArt

class MusicService : Service() {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat


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
        val previousIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val previousPendingIntent = PendingIntent.getBroadcast(baseContext, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // for notification image icon
        val imageArt = getImageArt(PlayerActivity.musicListPa[PlayerActivity.songPosition].path)
        val image = if (imageArt != null) {
            BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.music_icon)
        }

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
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
            .addAction(R.drawable.navigate_previous_back, "Previous", previousPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.navigate_next_icon, "Next", nextPendingIntent)
            .addAction(R.drawable.exit_icon, "Exit", exitPendingIntent)
            .build()


        startForeground(13, notification)
    }

}