package com.example.imusicplayer.Service.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.Model.Ui.PlayerActivity
import com.example.imusicplayer.Model.Ui.NowPlayingSong
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.exitApplication
import com.example.imusicplayer.Service.Domain.setSongPosition

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> prevNextSong(increment = false, context = context!!)
            ApplicationClass.PLAY -> {
                if (PlayerActivity.isPlaying) pauseMusicNotification() else playMusicNotification()
            }
            ApplicationClass.NEXT -> prevNextSong(increment = true, context = context!!)
            ApplicationClass.EXIT -> {
               exitApplication()
            }
        }
    }

    private fun playMusicNotification() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.pPlayPauseID.setIconResource(R.drawable.pause_icon)
        NowPlayingSong.binding.nowPlayingPlayPauseID.setIconResource(R.drawable.pause_icon)
    }

    private fun pauseMusicNotification() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.pPlayPauseID.setIconResource(R.drawable.play_icon)
        NowPlayingSong.binding.nowPlayingPlayPauseID.setIconResource(R.drawable.play_icon)
    }

    private fun prevNextSong(increment:Boolean, context: Context){
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createdMediaPlayer()
        Glide.with(context).load(PlayerActivity.musicListPa[PlayerActivity.songPosition].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(PlayerActivity.binding.pSongPicID)
        PlayerActivity.binding.pSongTittleID.text = PlayerActivity.musicListPa[PlayerActivity.songPosition].title
        playMusicNotification()
        //layout update for nowPlaying fragment from notification
        Glide.with(context).load(PlayerActivity.musicListPa[PlayerActivity.songPosition].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(NowPlayingSong.binding.nowPlayingImageID)
        NowPlayingSong.binding.nowPlayingSongTittleID.text = PlayerActivity.musicListPa[PlayerActivity.songPosition].title
    }
}