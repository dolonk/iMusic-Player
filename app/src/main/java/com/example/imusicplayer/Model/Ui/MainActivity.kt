package com.example.imusicplayer.Model.Ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imusicplayer.Model.Adapter.MusicAdapter
import com.example.imusicplayer.R
import com.example.imusicplayer.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.coolPinkNav)
        setContentView(binding.root)

        requestPermission()
        navigationBar()
        btnInitial()
        initialNavView()
        setRecyclerView()

    }

    private fun setRecyclerView() {
        val musicList = ArrayList<String>()
        musicList.add("1song")

        binding.mainRecyclerID.setHasFixedSize(true)
        binding.mainRecyclerID.setItemViewCacheSize(13)
        binding.mainRecyclerID.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter =MusicAdapter(this@MainActivity,musicList)
        binding.mainRecyclerID.adapter = musicAdapter
        binding.mainTotalSongsID.text ="Total Song: "+musicAdapter.itemCount
    }

    private fun initialNavView() {
        binding.navViewID.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedbackID -> Toast.makeText(baseContext, "FeedBack", Toast.LENGTH_SHORT)
                    .show()
                R.id.navExitID -> exitProcess(1)
            }
            true
        }
    }


    // Requested Permission for Storage
    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
        }
    }


    //For Navigation Bar
    private fun navigationBar() {
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }


    private fun btnInitial() {
        binding.shuffleBtnID.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }

        binding.favouriteBtnID.setOnClickListener {
            startActivity(Intent(this, FavouriteActivity::class.java))
        }

        binding.playlistBtnID.setOnClickListener {
            startActivity(Intent(this, PlayListActivity::class.java))
        }
    }


}