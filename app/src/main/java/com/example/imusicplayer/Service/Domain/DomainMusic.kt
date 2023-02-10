package com.example.imusicplayer.Service.Domain

import android.media.MediaMetadataRetriever
import com.example.imusicplayer.Model.Ui.FavouriteActivity
import com.example.imusicplayer.Model.Ui.PlayerActivity
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class DomainMusic(
    var id: String, var title: String,
    var album: String, var artist: String,
    var duration: Long = 0, var path: String,
    var imageUri: String
)

class Playlist(){
    lateinit var name: String
    lateinit var plyList: ArrayList<DomainMusic>
    lateinit var createdBy: String
    lateinit var createdOn: String
}

class RefPlaylist(){
    var ref: ArrayList<Playlist> = ArrayList()
}

fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(
        duration, TimeUnit.MILLISECONDS
    ) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%2d:%2d", minutes, seconds)
}

fun setSongPosition(increment: Boolean) {
    if (!PlayerActivity.repeat){
        if (increment) {
            if (PlayerActivity.musicListPa.size - 1 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = 0
            else ++PlayerActivity.songPosition
        } else
            if (0 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = PlayerActivity.musicListPa.size - 1
            else --PlayerActivity.songPosition
    }
}

fun getImageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun exitApplication(){
    if (PlayerActivity.musicService != null) {
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null
    }
    exitProcess(1)
}

fun favouriteChecker(id: String): Int{
    PlayerActivity.isFavourite = false
    FavouriteActivity.favouriteSong.forEachIndexed { index, domainMusic ->
        if (id == domainMusic.id){
            PlayerActivity.isFavourite = true
            return index
        }
    }
    return -1
}
