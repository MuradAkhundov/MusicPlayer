package com.muradakhundov.musicplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import com.muradakhundov.musicplayer.MusicFiles
import com.muradakhundov.musicplayer.PlayerActivity.Companion.listSongs
import com.muradakhundov.musicplayer.tool.ActionPlaying


class MusicService : Service(), OnCompletionListener {
    private lateinit var actionPlaying: ActionPlaying
    var mBinder: IBinder = MyBinder()
    var mediaplayer = MediaPlayer()
    var musicFiles: ArrayList<MusicFiles> = ArrayList()
    var uri: Uri? = null
    var position = -1
    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var myPosition = intent?.getIntExtra("servicePosition", -1)
        var actionName = intent?.getStringExtra("actionName")
        if (myPosition != -1 && myPosition != null ) {
            playMedia(myPosition)
        }
        if (actionName != null){
            when(actionName){
                "playPause" -> {
                    if (actionPlaying != null){
                        actionPlaying.playPauseBtnClicked()
                    }
                }
                "next" -> {
                    if (actionPlaying != null) {
                        actionPlaying.nextBtnClicked()
                    }
                }
                "previous" -> {
                    if (actionPlaying != null) {
                        actionPlaying.prevBtnClicked()
                    }
                }
            }
        }
        return START_STICKY

    }

    private fun playMedia(startposition: Int) {
        musicFiles = listSongs
        position = startposition
        if (mediaplayer != null) {
            mediaplayer.stop()
            mediaplayer.release()
            if (musicFiles != null) {
                createMediaPLayer(position)
                mediaplayer.start()
            }
        }
        else {
            createMediaPLayer(position)
            mediaplayer.start()
        }
    }


    inner class MyBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    fun start() {
        mediaplayer.start()
    }

    fun isPlaying(): Boolean {
        return mediaplayer.isPlaying
    }

    fun stop() {
        mediaplayer.stop()
    }

    fun release() {
        mediaplayer.release()
    }

    fun seekTo(position: Int) {
        mediaplayer.seekTo(position)
    }

    fun getDuration(): Int {
        return mediaplayer.duration
    }

    fun createMediaPLayer(positionInner: Int) {
        position =positionInner
        uri = Uri.parse(musicFiles.get(position).path)
        mediaplayer = MediaPlayer.create(baseContext, uri)
    }

    fun getCurrentPosition(): Int {
        return mediaplayer.currentPosition
    }

    fun pause() {
        mediaplayer.pause()
    }

    fun onCompleted() {
        mediaplayer.setOnCompletionListener(this)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (actionPlaying != null) {
            position = (position + 1) % musicFiles.size
            actionPlaying.nextBtnClicked()
        }
        if(mediaplayer !=null){
            createMediaPLayer(position)
            mediaplayer.start()
            onCompleted()
        }

    }


    fun setCallBack(actionPlaying : ActionPlaying){
        this.actionPlaying = actionPlaying
    }
}