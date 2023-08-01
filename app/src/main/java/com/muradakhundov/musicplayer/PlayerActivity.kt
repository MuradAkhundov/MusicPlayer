package com.muradakhundov.musicplayer

import android.icu.number.IntegerWidth
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.bumptech.glide.Glide
import com.muradakhundov.musicplayer.MainActivity.Companion.musicFiles
import com.muradakhundov.musicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPlayerBinding
    lateinit var uri : Uri
    lateinit var mediaPlayer : MediaPlayer
    var position = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)

        mediaPlayer = MediaPlayer()

        //playThread , previousThread , nextThread

        getIntentMethod()

        binding.songName.text = listSongs.get(position).title
        binding.songArtist.text = listSongs.get(position).title
        binding.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (mediaPlayer !=null && fromUser){
                    mediaPlayer.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        this.runOnUiThread(object :Runnable{
            override fun run() {
                if (mediaPlayer != null){
                    var mCurrentPosition = (mediaPlayer.currentPosition / 1000)
                    binding.seekbar.progress = mCurrentPosition
                    binding.durationPlayed.text = formattedTime(mCurrentPosition)
                }
                Handler().postDelayed(this,1000)
            }

        })

        setContentView(binding.root)
    }

    fun formattedTime(mCurrentPosition: Int) : String {
        var totalout = ""
        var totalNew = ""
        var seconds = (mCurrentPosition % 60).toString()
        var minutes = (mCurrentPosition / 60).toString()
        totalout = "$minutes:$seconds"
        totalNew = "$minutes:0$seconds"
        return if (seconds.length == 1){
            totalNew
        } else{
            totalout
        }
    }

    fun getIntentMethod(){
        position = intent.getIntExtra("position",-1)
        listSongs = musicFiles
        if (listSongs != null){
            binding.playPause.setImageResource(R.drawable.pause_ic)
            uri = Uri.parse(listSongs.get(position).path)
        }
        if (mediaPlayer != null){
            mediaPlayer.stop()
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(applicationContext,uri)
            mediaPlayer.start()
        }
        else{
            mediaPlayer = MediaPlayer.create(applicationContext,uri)
            mediaPlayer.start()
        }
        binding.seekbar.max = mediaPlayer.duration / 1000
        metaData(uri)
    }

    fun metaData(uri : Uri){
        var retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        var durationTotal = Integer.parseInt(listSongs.get(position).duration) / 1000
        binding.durationTotal.text = formattedTime(durationTotal)
        var art : ByteArray? = retriever.embeddedPicture
        if (art != null){
            Glide.with(this)
                .asBitmap()
                .load(art)
                .into(binding.coverArt)
        }
        else{
            Glide.with(this)
                .asBitmap()
                .load(R.drawable.defaultimg)
                .into(binding.coverArt)
        }
    }


    override fun onResume() {
        super.onResume()
        playThreadBtn()
        nextThreadBtn()
        prevThreadBtn()

    }

    private fun prevThreadBtn() {
        var prevThread = Thread{
            binding.previous.setOnClickListener {
                prevBtnClicked()
            }
        }
        prevThread.start()

    }

    private fun prevBtnClicked() {
        if (mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.release()
            position = if (position - 1 < 0) listSongs.size - 1 else position - 1
            uri = Uri.parse(listSongs.get(position).path)
            mediaPlayer = MediaPlayer.create(applicationContext,uri)
            metaData(uri)
            binding.songName.text = listSongs.get(position).title
            binding.songArtist.text = listSongs.get(position).artist
            this.runOnUiThread(object :Runnable{
                override fun run() {
                    if (mediaPlayer != null){
                        var mCurrentPosition = (mediaPlayer.currentPosition / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this,1000)
                }

            })
            binding.playPause.setImageResource(R.drawable.pause_ic)
            mediaPlayer.start()
        }
        else{
            mediaPlayer.stop()
            mediaPlayer.release()
            position = if (position - 1 < 0) listSongs.size - 1 else position - 1
            uri = Uri.parse(listSongs.get(position).path)
            mediaPlayer = MediaPlayer.create(applicationContext,uri)
            metaData(uri)
            binding.songName.text = listSongs.get(position).title
            binding.songArtist.text = listSongs.get(position).artist
            this.runOnUiThread(object :Runnable{
                override fun run() {
                    if (mediaPlayer != null){
                        var mCurrentPosition = (mediaPlayer.currentPosition / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this,1000)
                }

            })
            binding.playPause.setImageResource(R.drawable.play_ic)
        }
    }

    private fun nextThreadBtn() {
        var nextThread = Thread{
            binding.next.setOnClickListener {
                nextBtnClicked()
            }
        }
        nextThread.start()

    }



    private fun playThreadBtn() {
        var playThread = Thread{
            binding.playPause.setOnClickListener {
                playPauseBtnClicked()
            }
        }
        playThread.start()

    }

    private fun playPauseBtnClicked() {
        if (mediaPlayer.isPlaying){
            binding.playPause.setImageResource(R.drawable.play_ic)
            mediaPlayer.pause()
            binding.seekbar.max = mediaPlayer.duration / 1000
            this.runOnUiThread(object :Runnable{
                override fun run() {
                    if (mediaPlayer != null){
                        var mCurrentPosition = (mediaPlayer.currentPosition / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this,1000)
                }

            })
        }
        else{
            binding.playPause.setImageResource(R.drawable.pause_ic)
            mediaPlayer.start()
            binding.seekbar.max = mediaPlayer.duration / 1000
            this.runOnUiThread(object :Runnable{
                override fun run() {
                    if (mediaPlayer != null){
                        var mCurrentPosition = (mediaPlayer.currentPosition / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this,1000)
                }

            })
        }
    }

    private fun nextBtnClicked() {
        if (mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.release()
            position = ((position + 1) % listSongs.size)
            uri = Uri.parse(listSongs.get(position).path)
            mediaPlayer = MediaPlayer.create(applicationContext,uri)
            metaData(uri)
            binding.songName.text = listSongs.get(position).title
            binding.songArtist.text = listSongs.get(position).artist
            this.runOnUiThread(object :Runnable{
                override fun run() {
                    if (mediaPlayer != null){
                        var mCurrentPosition = (mediaPlayer.currentPosition / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this,1000)
                }

            })
            binding.playPause.setImageResource(R.drawable.pause_ic)
            mediaPlayer.start()
        }
        else{
            mediaPlayer.stop()
            mediaPlayer.release()
            position = ((position + 1) % listSongs.size)
            uri = Uri.parse(listSongs.get(position).path)
            mediaPlayer = MediaPlayer.create(applicationContext,uri)
            metaData(uri)
            binding.songName.text = listSongs.get(position).title
            binding.songArtist.text = listSongs.get(position).artist
            this.runOnUiThread(object :Runnable{
                override fun run() {
                    if (mediaPlayer != null){
                        var mCurrentPosition = (mediaPlayer.currentPosition / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this,1000)
                }

            })
            binding.playPause.setImageResource(R.drawable.play_ic)
        }
    }

    companion object{
        var listSongs = ArrayList<MusicFiles>()
    }
}