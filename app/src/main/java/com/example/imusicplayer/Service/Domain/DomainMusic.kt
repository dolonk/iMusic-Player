package com.example.imusicplayer.Service.Domain

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.provider.ContactsContract.RawContacts.Data
import com.example.imusicplayer.Model.Ui.FavouriteActivity
import com.example.imusicplayer.Model.Ui.PlayerActivity
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class DomainMusic(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val imageUri: String
)

class Playlist() {
    lateinit var name: String
    lateinit var plyList: ArrayList<DomainMusic>
    lateinit var createdBy: String
    lateinit var createdOn: String
}

class RefPlaylist() {
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
    if (!PlayerActivity.repeat) {
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

fun exitApplication() {
    if (PlayerActivity.musicService != null) {
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null
    }
    exitProcess(1)
}

fun favouriteChecker(id: String): Int {
    PlayerActivity.isFavourite = false
    FavouriteActivity.favouriteSong.forEachIndexed { index, domainMusic ->
        if (id == domainMusic.id) {
            PlayerActivity.isFavourite = true
            return index
        }
    }
    return -1
}

fun checkPlaylist(playlist: ArrayList<DomainMusic>): ArrayList<DomainMusic>{
    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if(!file.exists())
            playlist.removeAt(index)
    }
    return playlist
}

fun getMainColor(img: Bitmap): Int {
    val newImg = Bitmap.createScaledBitmap(img, 1, 1, true)
    val color = newImg.getPixel(0, 0)
    newImg.recycle()
    return color
}
