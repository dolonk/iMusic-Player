package com.example.imusicplayer.Model.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.imusicplayer.databinding.MusicViewBinding

class MusicAdapter(private val context: Context, private val musicList: ArrayList<String>) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>() {
    class ViewHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        var tittle = binding.musicViewSongTittleID
        var album = binding.musicViewSongAlbumID
        var duration = binding.musicViewSongDurationID
        var image = binding.musicViewImageViewID

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tittle.text = musicList[position]
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}