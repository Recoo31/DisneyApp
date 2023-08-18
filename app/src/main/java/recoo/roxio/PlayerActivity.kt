package recoo.roxio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.View
import android.view.WindowManager
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.drm.DefaultDrmSessionManagerProvider
import com.google.android.exoplayer2.source.hls.*
import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistParserFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.MimeTypes

class PlayerActivity : AppCompatActivity() {
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initializePlayer()
    }

    private fun initializePlayer() {
        setupFullScreenMode()
        val extras = intent.extras
        val url = extras?.getString("url")
        val token = extras?.getString("token")

        val headers = mutableMapOf<String, String>()

        headers["Authorization"] = "Bearer $token"
        headers["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"

        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36")
            .setDefaultRequestProperties(headers) // Set headers for both media and DRM requests
            .setTransferListener(
                DefaultBandwidthMeter.Builder(this)
                    .setResetOnNetworkTypeChange(false)
                    .build()
            )

        val hlsMediaSourceFactory = HlsMediaSource.Factory(defaultHttpDataSourceFactory)
            .setAllowChunklessPreparation(true)
            .setPlaylistParserFactory(DefaultHlsPlaylistParserFactory())
            .setDrmSessionManagerProvider(DefaultDrmSessionManagerProvider())

        val hlsMediaSource = hlsMediaSourceFactory.createMediaSource(
            MediaItem.Builder()
                .setUri(Uri.parse(url))
                .setDrmConfiguration(
                    MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                        .setLicenseUri("https://disney.playback.edge.bamgrid.com/widevine/v1/obtain-license")
                        .setLicenseRequestHeaders(headers)
                        .build()
                )
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .setTag(null)
                .build()
        )

        // Prepare the player.
        exoPlayer = ExoPlayer.Builder(this)
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build()
        val playerView = findViewById<StyledPlayerView>(R.id.playerView)

        playerView.player = exoPlayer
        exoPlayer.playWhenReady = true
        exoPlayer.setMediaSource(hlsMediaSource, true)
        exoPlayer.prepare()
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.playWhenReady = false
    }

    private fun releaseResource() {
        exoPlayer.release()
    }

    override fun onDestroy() {
        releaseResource()
        super.onDestroy()
    }

    private fun setupFullScreenMode() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
