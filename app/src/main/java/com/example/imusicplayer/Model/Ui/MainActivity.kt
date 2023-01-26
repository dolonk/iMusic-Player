package com.example.imusicplayer.Model.Ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imusicplayer.Model.Adapter.MusicAdapter
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object{
        lateinit var  MusicListMA: ArrayList<DomainMusic>
    }

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
        getAllMusic()

    }

    @SuppressLint("Range")
    private fun getAllMusic(): ArrayList<DomainMusic> {
        val tempList = ArrayList<DomainMusic>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val projectionManager = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projectionManager, selection,
            null, null, null,
            //MediaStore.Audio.Media.DATE_ADDED + "DESC"
        )
        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)) ?: "Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))?:"Unknown"
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))

                    //Song ImageView icon load
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val imageUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                    val music = DomainMusic(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC, imageUri = imageUriC)
                    val file = File(music.path)
                    if(file.exists())
                        tempList.add(music)

                } while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }


    @SuppressLint("SetTextI18n")
    private fun setRecyclerView() {
        MusicListMA =getAllMusic()
        binding.mainRecyclerID.setHasFixedSize(true)
        binding.mainRecyclerID.setItemViewCacheSize(13)
        binding.mainRecyclerID.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.mainRecyclerID.adapter = musicAdapter
        binding.mainTotalSongsID.text = "Total Song: " + musicAdapter.itemCount
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

}