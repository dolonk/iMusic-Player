package com.example.imusicplayer.Service.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.Model.Ui.PlayerActivity
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.setSongPosition
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> prevNextSong(increment = false, context = context!!)
            ApplicationClass.PLAY -> {
                if (PlayerActivity.isPlaying) pauseMusicNotification() else playMusicNotification()
            }
            ApplicationClass.NEXT -> prevNextSong(increment = true, context = context!!)
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

    private fun prevNextSong(increment:Boolean, context: Context){
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createdMediaPlayer()
        Glide.with(context).load(PlayerActivity.musicListPa[PlayerActivity.songPosition].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(PlayerActivity.binding.pSongPicID)
        PlayerActivity.binding.pSongTittleID.text = PlayerActivity.musicListPa[PlayerActivity.songPosition].title
        playMusicNotification()
    }
}