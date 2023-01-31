package com.example.imusicplayer.Service.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.imusicplayer.Model.Ui.PlayerActivity
import com.example.imusicplayer.R
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> Toast.makeText(
                context,
                "Previous Clicked",
                Toast.LENGTH_SHORT
            ).show()
            ApplicationClass.PLAY -> {
                if (PlayerActivity.isPlaying) pauseMusicNotification() else playMusicNotification()
            }
            ApplicationClass.EXIT -> {
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService = null
                exitProcess(1)
            }
        }
    }

    private fun playMusicNotification() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
    }

    private fun pauseMusicNotification() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.pPlayPauseID.setIconResource(R.drawable.play_icon)
    }
}