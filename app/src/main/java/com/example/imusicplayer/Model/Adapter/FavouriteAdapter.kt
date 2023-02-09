package com.example.imusicplayer.Model.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.Model.Ui.PlayerActivity
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.databinding.ActivityFavouriteBinding
import com.example.imusicplayer.databinding.FavouriteViewItemBinding
import com.example.imusicplayer.databinding.MusicViewBinding

class FavouriteAdapter(
    private var context: Context,
    private var musicList: ArrayList<DomainMusic>
) :
    RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {
    class ViewHolder(binding: FavouriteViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageF = binding.fVImageId
        val songTittleF = binding.fVSongTittleId
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FavouriteViewItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(musicList[position].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(holder.imageF)
        holder.songTittleF.text = musicList[position].title
        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "FavouriteAdapter")
            ContextCompat.startActivity(context, intent, null)
        }
    }
}