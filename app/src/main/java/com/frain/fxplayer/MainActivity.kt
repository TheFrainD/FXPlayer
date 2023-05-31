package com.frain.fxplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.frain.fxplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var binding: ActivityMainBinding
    private var exoPlayer: ExoPlayer? = null
    private var playWhenReady = true
    private var playbackPosition = 0L

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.also { uri ->
                preparePlayer(uri)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView()

        binding.loadMediaButton.setOnClickListener {
            openVideoSelector()
        }
    }

    private fun setView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun preparePlayer(videoUri: Uri) {
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = exoPlayer
        exoPlayer?.apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            seekTo(playbackPosition)
            playWhenReady = playWhenReady
            prepare()
        }
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun openVideoSelector() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            val mimeTypes = arrayOf("video/*", "audio/*")
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }

        getResult.launch(intent)
    }

}