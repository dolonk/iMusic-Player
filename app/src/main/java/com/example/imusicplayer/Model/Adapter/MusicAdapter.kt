package com.example.imusicplayer.Model.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.Model.Ui.MainActivity
import com.example.imusicplayer.Model.Ui.PlayerActivity
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.Service.Domain.formatDuration
import com.example.imusicplayer.databinding.MusicViewBinding

class MusicAdapter(private var context: Context, private var musicList: ArrayList<DomainMusic>) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>() {
    class ViewHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        var tittle = binding.musicViewSongTittleID
        var album = binding.musicViewSongAlbumID
        var duration = binding.musicViewSongDurationID
        var image = binding.musicViewImageViewID
        var root = binding.musicViewCardViewID

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tittle.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        holder.tittle.text = musicList[position].title

        Glide.with(context).load(musicList[position].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(holder.image)

        holder.root.setOnClickListener {
            when{
                MainActivity.search -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                else -> sendIntent(ref = "MusicAdapter", pos = position)
            }
        }
    }

    private fun sendIntent(ref: String, pos:Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList: ArrayList<DomainMusic>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
}