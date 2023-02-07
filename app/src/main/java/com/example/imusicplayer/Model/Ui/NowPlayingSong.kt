package com.example.imusicplayer.Model.Ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.imusicplayer.R
import com.example.imusicplayer.Service.Domain.setSongPosition
import com.example.imusicplayer.databinding.FragmentNowPlayingSongBinding

class NowPlayingSong : Fragment() {


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingSongBinding
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing_song, container, false)
        binding = FragmentNowPlayingSongBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        setPlayPauseBtn()
        setNextSongBtn()
        setIntent()
        return view
    }

    private fun setIntent() {
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index",PlayerActivity.songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
    }

    private fun setNextSongBtn() {
        binding.nowPlayingNextID.setOnClickListener {
            setSongPosition(increment = true)
            PlayerActivity.musicService!!.createdMediaPlayer()
            Glide.with(this).load(PlayerActivity.musicListPa[PlayerActivity.songPosition].imageUri)
                .apply(RequestOptions().placeholder(R.drawable.music_icon))
                .into(binding.nowPlayingImageID)
            binding.nowPlayingSongTittleID.text =
                PlayerActivity.musicListPa[PlayerActivity.songPosition].title
            binding.nowPlayingNextID.text =
                PlayerActivity.musicListPa[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            setPlaySong()
        }
    }

    private fun setPlayPauseBtn() {
        binding.nowPlayingPlayPauseID.setOnClickListener {
            if (PlayerActivity.isPlaying) setPauseSong() else setPlaySong()
        }
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {
            binding.root.visibility = View.VISIBLE
            // for moving line Text
            binding.nowPlayingSongTittleID.isSelected = true

            //Layout initialize for activity
            Glide.with(this).load(PlayerActivity.musicListPa[PlayerActivity.songPosition].imageUri)
                .apply(RequestOptions().placeholder(R.drawable.music_icon))
                .into(binding.nowPlayingImageID)
            binding.nowPlayingSongTittleID.text = PlayerActivity.musicListPa[PlayerActivity.songPosition].title
            if (PlayerActivity.isPlaying) {
                binding.nowPlayingPlayPauseID.setIconResource(R.drawable.pause_icon)
            } else binding.nowPlayingPlayPauseID.setIconResource(R.drawable.play_icon)
        }
    }

    private fun setPauseSong() {
        binding.nowPlayingPlayPauseID.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.pSongNextID.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
    }

    private fun setPlaySong() {
        binding.nowPlayingPlayPauseID.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.pSongNextID.setIconResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
    }

}