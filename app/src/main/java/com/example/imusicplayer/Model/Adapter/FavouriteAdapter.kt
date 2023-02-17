package com.example.imusicplayer.Model.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.Model.Ui.PlayNextActivity
import com.example.imusicplayer.Model.Ui.PlayerActivity
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.databinding.ActivityFavouriteBinding
import com.example.imusicplayer.databinding.FavouriteViewItemBinding
import com.example.imusicplayer.databinding.MoreFeaturesBinding
import com.example.imusicplayer.databinding.MusicViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class FavouriteAdapter(
    private var context: Context,
    private var musicList: ArrayList<DomainMusic>,
    val playNext: Boolean = false
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

    fun updateFavourites(newList: ArrayList<DomainMusic>) {
        musicList = ArrayList()
        musicList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songTittleF.text = musicList[position].title
        Glide.with(context)
            .load(musicList[position].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(holder.imageF)

        //when play next music is clicked
        if (playNext) {
            holder.root.setOnClickListener {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "PlayNextActivity")
                ContextCompat.startActivity(context, intent, null)
            }
            holder.root.setOnLongClickListener {
                val customDialog =
                    LayoutInflater.from(context).inflate(R.layout.more_features, holder.root, false)
                val bindingMF = MoreFeaturesBinding.bind(customDialog)
                val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
                    .create()
                dialog.show()
                dialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
                bindingMF.AddToPNBtn.text = "Remove"
                bindingMF.AddToPNBtn.setOnClickListener {
                    if (position == PlayerActivity.songPosition)
                        Snackbar.make(
                            (context as Activity).findViewById(R.id.linearLayoutPN),
                            "Can't Remove Currently Playing Song.", Snackbar.LENGTH_SHORT
                        ).show()
                    else {
                        if (PlayerActivity.songPosition < position && PlayerActivity.songPosition != 0) --PlayerActivity.songPosition
                        PlayNextActivity.playNextList.removeAt(position)
                        PlayerActivity.musicListPa.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    dialog.dismiss()
                }
                return@setOnLongClickListener true
            }
        } else {
            holder.root.setOnClickListener {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavouriteAdapter")
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }
}