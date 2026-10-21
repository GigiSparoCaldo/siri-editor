package com.example.sirincontroller

import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.view.KeyEvent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearMessageListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path != "/sirin_control") return

        val command = String(messageEvent.data, Charsets.UTF_8)
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        when (command) {
            "VOL_UP" -> {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI
                )
            }
            "VOL_DOWN" -> {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_SHOW_UI
                )
            }
            "PLAY_PAUSE" -> {
                val controller = getSirinController()
                if (controller != null) {
                    val state = controller.playbackState?.state
                    if (state == android.media.session.PlaybackState.STATE_PLAYING) {
                        controller.transportControls.pause()
                    } else {
                        controller.transportControls.play()
                    }
                } else {
                    audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
                    audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
                }
            }
            "SEEK_FWD_15" -> {
                val controller = getSirinController()
                if (controller != null) {
                    val currentPos = controller.playbackState?.position ?: 0L
                    controller.transportControls.seekTo(currentPos + 15_000L)
                }
            }
            "SEEK_BWD_15" -> {
                val controller = getSirinController()
                if (controller != null) {
                    val currentPos = controller.playbackState?.position ?: 0L
                    controller.transportControls.seekTo((currentPos - 15_000L).coerceAtLeast(0L))
                }
            }
        }
    }

    private fun getSirinController(): MediaController? {
        return try {
            val manager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            val component = ComponentName(this, MediaNotificationListener::class.java)
            val controllers = manager.getActiveSessions(component)
            controllers.firstOrNull { it.packageName.contains("sirin", ignoreCase = true) }
                ?: controllers.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
