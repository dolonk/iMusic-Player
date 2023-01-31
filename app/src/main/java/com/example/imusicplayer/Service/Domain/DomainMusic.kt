package com.example.imusicplayer.Service.Domain

import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit

data class DomainMusic(
    var id: String, var title: String,
    var album: String, var artist: String,
    var duration: Long = 0, var path: String,
    var imageUri: String
)

fun formatDuration(duration: Long):String{
    val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)- minutes*TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%2d:%2d",minutes,seconds)
}

fun getImageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}