package com.example.imusicplayer.Model.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.Model.Ui.PlayListActivity
import com.example.imusicplayer.Model.Ui.PlaylistDetails
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.Playlist
import com.example.imusicplayer.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerListAdapter(
    private var context: Context,
    private var playList: ArrayList<Playlist>
) :
    RecyclerView.Adapter<PlayerListAdapter.ViewHolder>() {
    class ViewHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageP = binding.playlistImgId
        val songTittleP = binding.playlistNameId
        val root = binding.root
        val delete = binding.playlistDeleteId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songTittleP.text = playList[position].name
        holder.songTittleP.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playList[position].name)
                .setMessage("Do you want to delete playlist")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlayListActivity.refPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
        holder.root.setOnClickListener {
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }
        // playlist folder  image load
        if (PlayListActivity.refPlaylist.ref[position].plyList.size > 0) {
            Glide.with(context)
                .load(PlayListActivity.refPlaylist.ref[position].plyList[0].imageUri)
                .apply(RequestOptions().placeholder(R.drawable.music_icon))
                .into(holder.imageP)
        }
    }

    override fun getItemCount(): Int {
        return playList.size
    }

    fun refreshPlaylist() {
        playList = ArrayList()
        playList.addAll(PlayListActivity.refPlaylist.ref)
        notifyDataSetChanged()
    }
}




