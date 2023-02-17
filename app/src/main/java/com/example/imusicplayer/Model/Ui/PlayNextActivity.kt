package com.example.imusicplayer.Model.Ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imusicplayer.Model.Adapter.FavouriteAdapter
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.databinding.ActivityPlayNextBinding

class PlayNextActivity : AppCompatActivity() {
    companion object{
        var playNextList: ArrayList<DomainMusic> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        val binding = ActivityPlayNextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playNextRV.setHasFixedSize(true)
        binding.playNextRV.setItemViewCacheSize(13)
        binding.playNextRV.layoutManager = GridLayoutManager(this, 3)
        binding.playNextRV.adapter = FavouriteAdapter(this, playNextList, playNext = true)

        if(playNextList.isNotEmpty())
            binding.instructionPN.visibility = View.GONE

        binding.backBtnPN.setOnClickListener {
            finish()
        }
    }
}