package com.example.imusicplayer.Model.Ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.Model.Adapter.MusicAdapter
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.Service.Domain.RefPlaylist
import com.example.imusicplayer.Service.Domain.checkPlaylist
import com.example.imusicplayer.Service.Domain.exitApplication
import com.example.imusicplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object {
        var currentPlayListPosition = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnPD.setOnClickListener { finish() }

        currentPlayListPosition = intent.extras?.getInt("index") as Int

        // Data store refresh for song load
        try {
            PlayListActivity.refPlaylist.ref[currentPlayListPosition].plyList =
                checkPlaylist(playlist = PlayListActivity.refPlaylist.ref[currentPlayListPosition].plyList)
        } catch (e: Exception) {
            Toast.makeText(this,"Something wrong !", Toast.LENGTH_SHORT).show()
        }


        setRecyclerView()
        setShuffleBtn()
        setAddBtn()
        setRemoveBtn()

    }

    private fun setRemoveBtn() {
            binding.removeAllPD.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Remove")
                    .setMessage("Do you want to remove all song")
                    .setPositiveButton("Yes") { dialog, _ ->
                        PlayListActivity.refPlaylist.ref[currentPlayListPosition].plyList.clear()
                        adapter.refreshPlaylist()
                        dialog.dismiss()
                        recreate()
                        setLayout()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
    }

    private fun setAddBtn() {
        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
    }

    private fun setShuffleBtn() {
            binding.shuffleBtnPD.setOnClickListener {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("index", 0)
                intent.putExtra("class", "PlaylistDetailsShuffle")
                startActivity(intent)
            }
    }

    private fun setRecyclerView() {
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this@PlaylistDetails)
        adapter = MusicAdapter(
            this@PlaylistDetails,
            PlayListActivity.refPlaylist.ref[currentPlayListPosition].plyList,
            playlistDetails = true
        )
        binding.playlistDetailsRV.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        // for storing playList data using shared preference
        val editor = getSharedPreferences("PLAYLIST-DETAILS", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlayListActivity.refPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()

        binding.playlistNamePD.text =
            PlayListActivity.refPlaylist.ref[currentPlayListPosition].name
        setLayout()
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private  fun setLayout(){
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Song.\n\n" +
                "Created On: \n${PlayListActivity.refPlaylist.ref[currentPlayListPosition].createdOn}\n\n" +
                "--${PlayListActivity.refPlaylist.ref[currentPlayListPosition].createdBy}"
        if (adapter.itemCount > 0) {
            Glide.with(this)
                .load(PlayListActivity.refPlaylist.ref[currentPlayListPosition].plyList[0].imageUri)
                .apply(RequestOptions().placeholder(R.drawable.music_icon))
                .into(binding.playlistImgPD)
            binding.shuffleBtnPD.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()
        }
    }
}