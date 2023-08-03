package com.muradakhundov.musicplayer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.icu.number.IntegerWidth
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.muradakhundov.musicplayer.MainActivity.Companion.isRepeatOn
import com.muradakhundov.musicplayer.MainActivity.Companion.isShuffleOn
import com.muradakhundov.musicplayer.MainActivity.Companion.musicFiles
import com.muradakhundov.musicplayer.databinding.ActivityPlayerBinding
import kotlin.random.Random

class PlayerActivity : AppCompatActivity() , MediaPlayer.OnCompletionListener {
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
        mediaPlayer.setOnCompletionListener(this)

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

        binding.shuffle.setOnClickListener {
            if (isShuffleOn){
                isShuffleOn = false
                binding.shuffle.setImageResource(R.drawable.shuffle_off)
            }
            else{
                isShuffleOn = true
                binding.shuffle.setImageResource(R.drawable.shuffle_on)
            }
        }

        binding.repeat.setOnClickListener {
            if (isRepeatOn){
                isRepeatOn =false
                binding.repeat.setImageResource(R.drawable.repeat_off)
            }
            else{
                isRepeatOn =true
                binding.repeat.setImageResource(R.drawable.repeat_on)
            }
        }


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
            var bitmap = BitmapFactory.decodeByteArray(art,0,art.size)
            imageAnimation(this,binding.coverArt,bitmap)
            Palette.from(bitmap).generate(object : Palette.PaletteAsyncListener{
                override fun onGenerated(p0: Palette?) {
                    if (p0 != null){
                        var swatch = p0.dominantSwatch
                        var gradient = binding.imageViewGradient
                        var mContainer = binding.mContainer
                        gradient.setBackgroundColor(R.drawable.gradient_bg)
                        mContainer.setBackgroundResource(R.drawable.main_bg)
                        var gradientDrawable = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                        intArrayOf(swatch!!.rgb,0x00000000)
                        )
                        gradient.background = gradientDrawable
                        var gradientDrawableBg = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(swatch!!.rgb,swatch!!.rgb))
                        mContainer.background = gradientDrawableBg
                        binding.songName.setTextColor(swatch.titleTextColor)
                        binding.songArtist.setTextColor(swatch.bodyTextColor)

                    }
                    else{
                        var gradient = binding.imageViewGradient
                        var mContainer = binding.mContainer
                        gradient.setBackgroundColor(R.drawable.gradient_bg)
                        mContainer.setBackgroundResource(R.drawable.main_bg)
                        var gradientDrawable = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(0xff000000.toInt(),0x00000000)
                        )
                        gradient.background = gradientDrawable
                        var gradientDrawableBg = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(0xff000000.toInt(),0xff000000.toInt()))
                        mContainer.background = gradientDrawableBg
                        binding.songName.setTextColor(Color.WHITE)
                        binding.songArtist.setTextColor(Color.DKGRAY)

                    }
                }

            })
        }
        else{
            if (!isDestroyed){
                Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.defaultimg)
                    .into(binding.coverArt)
                binding.songName.setTextColor(Color.WHITE)
                binding.songArtist.setTextColor(Color.DKGRAY)
            }

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
            if (isShuffleOn && !isRepeatOn){
                position = getRandom(listSongs.size - 1)
            }
            else if (!isShuffleOn && !isRepeatOn){
                position = if (position - 1 < 0) listSongs.size - 1 else position - 1
            }
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
            mediaPlayer.setOnCompletionListener(this)
            binding.playPause.setBackgroundResource(R.drawable.pause_ic)
            mediaPlayer.start()
        }
        else{
            mediaPlayer.stop()
            mediaPlayer.release()
            if (isShuffleOn && !isRepeatOn){
                position = getRandom(listSongs.size - 1)
            }
            else if (!isShuffleOn && !isRepeatOn){
                position = if (position - 1 < 0) listSongs.size - 1 else position - 1
            }
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
            mediaPlayer.setOnCompletionListener(this)
            binding.playPause.setBackgroundResource(R.drawable.play_ic)
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

            if (isShuffleOn && !isRepeatOn){
                position = getRandom(listSongs.size - 1)
            }
            else if (!isShuffleOn && !isRepeatOn){
                position = ((position + 1) % listSongs.size)
            }
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
            mediaPlayer.setOnCompletionListener(this)
            binding.playPause.setBackgroundResource(R.drawable.pause_ic)
            mediaPlayer.start()
        }
        else{
            mediaPlayer.stop()
            mediaPlayer.release()
            if (isShuffleOn && !isRepeatOn){
                position = getRandom(listSongs.size - 1)
            }
            else if (!isShuffleOn && !isRepeatOn){
                position = ((position + 1) % listSongs.size)
            }
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
            mediaPlayer.setOnCompletionListener(this)
            binding.playPause.setBackgroundResource(R.drawable.play_ic)
        }
    }

    private fun getRandom(i: Int): Int {
        return Random.nextInt(i+1)
    }

    fun imageAnimation(context : Context,imageView: ImageView,bitmap: Bitmap){
        var animOut = AnimationUtils.loadAnimation(context,android.R.anim.fade_out)
        var animIn = AnimationUtils.loadAnimation(context,android.R.anim.fade_in)
        animOut.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                Glide.with(context).load(bitmap).into(imageView)
                animIn.setAnimationListener(object : Animation.AnimationListener{
                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
                imageView.startAnimation(animIn)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })
        imageView.startAnimation(animOut)
    }

    companion object{
        var listSongs = ArrayList<MusicFiles>()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextBtnClicked()
        if (mediaPlayer != null){
            Log.e("tag","salam")
            mediaPlayer = MediaPlayer.create(applicationContext,uri)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener(this)
        }
    }
}