package com.aftab.abentseller.Activities.Main

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.databinding.ActivityPlayVideoBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

@Suppress("deprecation")
class PlayVideoActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPlayVideoBinding

    private var playerView: PlayerView? = null
    private var exoPlayer: SimpleExoPlayer? = null
    var progressBar: ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        Functions.fullScreenWithNav(this)
        Functions.changeNavigationBarColor(this, R.color.black)
        val videoUrl = intent.getStringExtra(Constants.SEND_VIDEO_URL)
        viewsInit()
        progressBar!!.visibility = View.VISIBLE
        val control = DefaultLoadControl.Builder()
            .setBufferDurationsMs(100 * 1000, 200 * 1000, 1000, 5000)
            .createDefaultLoadControl()
        exoPlayer = SimpleExoPlayer.Builder(this).setLoadControl(control).build()
        val factory = DefaultDataSourceFactory(this, getString(R.string.app_name))
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        val source: MediaSource =
            ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem)
        exoPlayer!!.prepare(source, false, false)
        playWhenReady()
    }

    private fun viewsInit() {
        playerView = findViewById(R.id.player_view)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun playWhenReady() {
        exoPlayer!!.playWhenReady = true
        exoPlayer!!.volume = 0.50f
        exoPlayer!!.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer!!.addListener(object : Player.Listener {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar!!.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    progressBar!!.visibility = View.GONE
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
        })
        playerView!!.player = exoPlayer
    }

    override fun onPause() {
        super.onPause()
        exoPlayer!!.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer!!.pause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exoPlayer!!.pause()
        Functions.hideKeyboard(this)
    }
}