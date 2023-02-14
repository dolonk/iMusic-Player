package com.example.imusicplayer.Model.Ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imusicplayer.Model.Adapter.FavouriteAdapter
import com.example.imusicplayer.Model.Adapter.MusicAdapter
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.Service.Domain.checkPlaylist
import com.example.imusicplayer.databinding.ActivityFavouriteBinding
import com.example.imusicplayer.databinding.ActivityMainBinding

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var favouriteAdapter: FavouriteAdapter

    companion object{
        var favouriteSong: ArrayList<DomainMusic> = ArrayList()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnID.setOnClickListener { finish() }

        // Data store refresh for song load
        try {
            favouriteSong = checkPlaylist(favouriteSong)
        } catch (e: Exception) {
            Toast.makeText(this,"Something wrong !", Toast.LENGTH_SHORT).show()
        }



        setRecyclerView()
        setShuffleBtn()
    }

    private fun setShuffleBtn() {
        if (favouriteSong.size<2){
          binding.fVShuffleId.visibility = View.INVISIBLE
        }
        binding.fVShuffleId.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }
    }

    private fun setRecyclerView() {
        binding.fRecyclerViewId.setHasFixedSize(true)
        binding.fRecyclerViewId.setItemViewCacheSize(13)
        binding.fRecyclerViewId.layoutManager = GridLayoutManager(this, 3)
        favouriteAdapter = FavouriteAdapter(this, favouriteSong)
        binding.fRecyclerViewId.adapter = favouriteAdapter
    }
}