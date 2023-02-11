package com.example.imusicplayer.Model.Ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imusicplayer.Model.Adapter.MusicAdapter
import com.example.imusicplayer.Model.Adapter.PlayerListAdapter
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.Playlist
import com.example.imusicplayer.Service.Domain.RefPlaylist
import com.example.imusicplayer.Service.Domain.exitApplication
import com.example.imusicplayer.databinding.ActivityMainBinding
import com.example.imusicplayer.databinding.ActivityPlayListBinding
import com.example.imusicplayer.databinding.AddPlaylistDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlayListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayListBinding
    private lateinit var playlistAdapter: PlayerListAdapter

    companion object {
        var refPlaylist = RefPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerView()
        setCreatedPlayList()
    }

    private fun setCreatedPlayList() {
        binding.pLCreatedListId.setOnClickListener {
            //show bottom sheet dialog
            val customDialog = LayoutInflater.from(this)
                .inflate(R.layout.add_playlist_dialog, binding.root, false)

            // Take input data from created playlist
            val binder = AddPlaylistDialogBinding.bind(customDialog)

            val builder = MaterialAlertDialogBuilder(this)
            builder.setView(customDialog)
                .setTitle("Created PlayList")
                .setPositiveButton("ADD") { dialog, _ ->
                    // Take input data from created playlist
                    val playlistName = binder.playlistName.text
                    val createdBy = binder.yourName.text
                    if (playlistName != null) {
                        if (playlistName.isNotEmpty()) {
                            addPlayList(playlistName.toString(), createdBy.toString())
                        }
                    }
                    dialog.dismiss()
                }.show()
        }
    }

    private fun addPlayList(name: String, created: String) {
        var playlistExits = false
        for (i in refPlaylist.ref){
            if (name.equals(i.name)){
                playlistExits = true
                break
            }
        }
        if (playlistExits)
            Toast.makeText(this, "Playlist Exits!!", Toast.LENGTH_LONG).show()
        else{
            val tempList = Playlist()
            tempList.name = name
            tempList.plyList = ArrayList()
            tempList.createdBy = created
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempList.createdOn = sdf.format(calendar)
            refPlaylist.ref.add(tempList)
            playlistAdapter.refreshPlaylist()
        }
    }

    private fun setRecyclerView() {
        binding.pLRecyclerViewId.setHasFixedSize(true)
        binding.pLRecyclerViewId.setItemViewCacheSize(13)
        binding.pLRecyclerViewId.layoutManager = GridLayoutManager(this@PlayListActivity, 2)
        playlistAdapter = PlayerListAdapter(this@PlayListActivity, playList = refPlaylist.ref)
        binding.pLRecyclerViewId.adapter = playlistAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        playlistAdapter.notifyDataSetChanged()
    }
}