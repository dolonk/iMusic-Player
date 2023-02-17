package com.example.imusicplayer.Model.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.Model.Ui.*
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.Service.Domain.formatDuration
import com.example.imusicplayer.Service.Domain.setDialogBtnBackground
import com.example.imusicplayer.databinding.DetailsViewBinding
import com.example.imusicplayer.databinding.MoreFeaturesBinding
import com.example.imusicplayer.databinding.MusicViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MusicAdapter(
    private var context: Context,
    private var musicList: ArrayList<DomainMusic>,
    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false
) :
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

        //for play next feature
        if (!selectionActivity)
            holder.root.setOnLongClickListener {
                val customDialog =
                    LayoutInflater.from(context).inflate(R.layout.more_features, holder.root, false)
                val bindingMF = MoreFeaturesBinding.bind(customDialog)
                val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
                    .create()
                dialog.show()
                dialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))

                bindingMF.AddToPNBtn.setOnClickListener {
                    try {
                        if (PlayNextActivity.playNextList.isEmpty()) {
                            PlayNextActivity.playNextList.add(PlayerActivity.musicListPa[PlayerActivity.songPosition])
                            PlayerActivity.songPosition = 0
                        }
                        PlayNextActivity.playNextList.add(musicList[position])
                        PlayerActivity.musicListPa = ArrayList()
                        PlayerActivity.musicListPa.addAll(PlayNextActivity.playNextList)
                    } catch (e: Exception) {
                        Snackbar.make(context, holder.root, "Play A Song First!!", 3000).show()
                    }
                    dialog.dismiss()
                }

                bindingMF.infoBtn.setOnClickListener {
                    dialog.dismiss()
                    val detailsDialog = LayoutInflater.from(context)
                        .inflate(R.layout.details_view, bindingMF.root, false)
                    val binder = DetailsViewBinding.bind(detailsDialog)
                    binder.detailsTV.setTextColor(Color.WHITE)
                    binder.root.setBackgroundColor(Color.TRANSPARENT)
                    val dDialog = MaterialAlertDialogBuilder(context)
//                        .setBackground(ColorDrawable(0x99000000.toInt()))
                        .setView(detailsDialog)
                        .setPositiveButton("OK") { self, _ -> self.dismiss() }
                        .setCancelable(false)
                        .create()
                    dDialog.show()
                    dDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    setDialogBtnBackground(context, dDialog)
                    dDialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
                    val str = SpannableStringBuilder().bold { append("DETAILS\n\nName: ") }
                        .append(musicList[position].title)
                        .bold { append("\n\nDuration: ") }
                        .append(DateUtils.formatElapsedTime(musicList[position].duration / 1000))
                        .bold { append("\n\nLocation: ") }.append(musicList[position].path)
                    binder.detailsTV.text = str
                }

                return@setOnLongClickListener true
            }

        when {
            selectionActivity -> {
                holder.root.setOnClickListener {
                    if (addSong(musicList[position]))
                        holder.root.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.pink_color
                            )
                        )
                    else
                        holder.root.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                }
            }
            playlistDetails -> {
                holder.root.setOnClickListener {
                    sendIntent("PlaylistDetailsAdapter", pos = position)
                }
            }
            else -> {
                holder.root.setOnClickListener {
                    when {
                        MainActivity.search -> sendIntent(
                            ref = "MusicAdapterSearch",
                            pos = position
                        )
                        musicList[position].id == PlayerActivity.nowPlayingSongId -> {
                            sendIntent("NowPlaying", pos = PlayerActivity.songPosition)
                        }
                        else -> sendIntent(ref = "MusicAdapter", pos = position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    private fun addSong(song: DomainMusic): Boolean {
        PlayListActivity.refPlaylist.ref[PlaylistDetails.currentPlayListPosition].plyList
            .forEachIndexed { index, domainMusic ->
                if (song.id == domainMusic.id) {
                    PlayListActivity.refPlaylist.ref[PlaylistDetails.currentPlayListPosition]
                        .plyList.removeAt(index)
                    return false
                }
            }
        PlayListActivity.refPlaylist.ref[PlaylistDetails.currentPlayListPosition]
            .plyList.add(song)
        return true
    }

     fun refreshPlaylist() {
        musicList = ArrayList()
        musicList = PlayListActivity.refPlaylist.ref[PlaylistDetails.currentPlayListPosition].plyList
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    fun updateMusicList(searchList: ArrayList<DomainMusic>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
}