package com.example.imusicplayer.Model.Ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imusicplayer.Model.Adapter.MusicAdapter
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.DomainMusic
import com.example.imusicplayer.Service.Domain.RefPlaylist
import com.example.imusicplayer.Service.Domain.exitApplication
import com.example.imusicplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var MusicListMA: ArrayList<DomainMusic>
        lateinit var musicListSearch: ArrayList<DomainMusic>
        var search: Boolean = false
        var themeIndex = 0
        val currentTheme = arrayOf(R.style.coolPink, R.style.coolBlue, R.style.coolPurple,
            R.style.coolGreen, R.style.coolBlack)
        val currentThemeNav = arrayOf(R.style.coolPinkNav, R.style.coolBlueNav, R.style.coolPurpleNav,
            R.style.coolGreenNav, R.style.coolBlackNav)
        val currentGradient = arrayOf(R.drawable.gradient_pink, R.drawable.gradient_blue, R.drawable.gradient_purple,
            R.drawable.gradient_green, R.drawable.gradient_black)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        // For theme apply
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex",0)
        setTheme(currentThemeNav[themeIndex])
        setContentView(binding.root)

        setNavigationBar()
        btnInitial()
        setNavItemsView()
        if (requestPermission()) {
            setRecyclerView()

            // for retrieve favourite data using shared preference
            FavouriteActivity.favouriteSong = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSong", null)
            val typeToken = object : TypeToken<ArrayList<DomainMusic>>() {}.type
            if (jsonString != null) {
                val data: ArrayList<DomainMusic> =
                    GsonBuilder().create().fromJson(jsonString, typeToken)
                FavouriteActivity.favouriteSong.addAll(data)
            }

            // for retrieve playlist data using shared preference
            PlayListActivity.refPlaylist = RefPlaylist()
            val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
            if (jsonStringPlaylist != null) {
                val dataPlaylist: RefPlaylist =
                    GsonBuilder().create().fromJson(jsonStringPlaylist, RefPlaylist::class.java)
                PlayListActivity.refPlaylist = dataPlaylist
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setRecyclerView() {
        search = false
        MusicListMA = getAllMusic()
        binding.mainRecyclerID.setHasFixedSize(true)
        binding.mainRecyclerID.setItemViewCacheSize(13)
        binding.mainRecyclerID.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.mainRecyclerID.adapter = musicAdapter
        binding.mainTotalSongsID.text = "Total Songs : " + musicAdapter.itemCount
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
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                            ?: "Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                        ?: "Unknown"
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                            ?: "Unknown"
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                            ?: "Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))

                    //Song ImageView icon load
                    val albumIdC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val imageUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                    val music = DomainMusic(
                        id = idC,
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        path = pathC,
                        duration = durationC,
                        imageUri = imageUriC
                    )
                    val file = File(music.path)
                    if (file.exists())
                        tempList.add(music)

                } while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }


    private fun setNavItemsView() {
        binding.navViewID.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedbackID -> startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))
                R.id.navSettingID -> startActivity(Intent(this, SettingActivity::class.java))
                R.id.navAbout -> startActivity(Intent(this, AboutActivity::class.java))
                R.id.navExitID -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close iMusic App")
                        .setPositiveButton("Yes") { _, _ ->
                            exitApplication()
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
            true
        }
    }


    private fun btnInitial() {
        binding.shuffleBtnID.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
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
    private fun setNavigationBar() {
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
    private fun requestPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                setRecyclerView()
            } else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
        }
    }

    override fun onResume() {
        super.onResume()

        // for storing favourite data using shared preference
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouriteSong)
        editor.putString("FavouriteSong", jsonString)

        // for storing playList data using shared preference
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlayListActivity.refPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null) {
            exitApplication()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        // for setting gradiant
        findViewById<ConstraintLayout>(R.id.constrainLayoutNav)?.setBackgroundResource(
            currentGradient[themeIndex])
        val searchView =
            menu?.findItem(R.id.app_bar_search)?.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MusicListMA) {
                        if (song.title.lowercase().contains(userInput)) {
                            musicListSearch.add(song)
                            search = true
                            musicAdapter.updateMusicList(searchList = musicListSearch)
                        }
                    }
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}