package com.example.imusicplayer.Model.Ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imusicplayer.Model.Adapter.MusicAdapter
import com.example.imusicplayer.R
import com.example.imusicplayer.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnSA.setOnClickListener { finish() }

        setRecyclerView()
        setSearchView()
    }

    private fun setSearchView() {
        binding.searchViewSA.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if (newText != null){
                    val  userInput = newText.lowercase()
                    for (song in MainActivity.MusicListMA){
                        if (song.title.lowercase().contains(userInput)){
                            MainActivity.musicListSearch.add(song)
                            MainActivity.search = true
                            adapter.updateMusicList(searchList = MainActivity.musicListSearch)
                        }
                    }
                }
                return true
            }
        })
    }

    private fun setRecyclerView() {
        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.setItemViewCacheSize(13)
        binding.selectionRV.layoutManager = LinearLayoutManager(this@SelectionActivity)
        adapter = MusicAdapter(
            this@SelectionActivity,
            MainActivity.MusicListMA, selectionActivity = true
        )
        binding.selectionRV.adapter = adapter
    }
}