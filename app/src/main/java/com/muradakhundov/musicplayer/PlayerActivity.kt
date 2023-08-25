package com.muradakhundov.musicplayer

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.icu.number.IntegerWidth
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.muradakhundov.musicplayer.MainActivity.Companion.isRepeatOn
import com.muradakhundov.musicplayer.MainActivity.Companion.isShuffleOn
import com.muradakhundov.musicplayer.MainActivity.Companion.musicFiles
import com.muradakhundov.musicplayer.adapter.AlbumDetailsAdapter
import com.muradakhundov.musicplayer.adapter.MusicAdapter.Companion.getList
import com.muradakhundov.musicplayer.databinding.ActivityPlayerBinding
import com.muradakhundov.musicplayer.receiver.NotificationReceiver
import com.muradakhundov.musicplayer.service.ApplicationClass.Companion.ACTION_NEXT
import com.muradakhundov.musicplayer.service.ApplicationClass.Companion.ACTION_PLAY
import com.muradakhundov.musicplayer.service.ApplicationClass.Companion.ACTION_PREVIOUS
import com.muradakhundov.musicplayer.service.ApplicationClass.Companion.CHANNEL_ID_2
import com.muradakhundov.musicplayer.service.MusicService
import com.muradakhundov.musicplayer.tool.ActionPlaying
import kotlin.random.Random

class PlayerActivity : AppCompatActivity(), ActionPlaying,
    ServiceConnection {
    private lateinit var binding: ActivityPlayerBinding
    lateinit var uri: Uri

    //    lateinit var mediaPlayer : MediaPlayer
    var position = 1
    private lateinit var mediaSession: MediaSessionCompat
    lateinit var musicService: MusicService

    companion object {
        var listSongs = ArrayList<MusicFiles>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        mediaSession = MediaSessionCompat(baseContext, "My Audio")
//        mediaPlayer = MediaPlayer()
        musicService = MusicService()
        //playThread , previousThread , nextThread

        getIntentMethod()

        binding.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        this.runOnUiThread(object : Runnable {
            override fun run() {
                if (musicService != null) {
                    var mCurrentPosition = (musicService.getCurrentPosition() / 1000)
                    binding.seekbar.progress = mCurrentPosition
                    binding.durationPlayed.text = formattedTime(mCurrentPosition)
                }
                Handler().postDelayed(this, 1000)
            }

        })

        binding.shuffle.setOnClickListener {
            if (isShuffleOn) {
                isShuffleOn = false
                binding.shuffle.setImageResource(R.drawable.shuffle_off)
            } else {
                isShuffleOn = true
                binding.shuffle.setImageResource(R.drawable.shuffle_on)
            }
        }

        binding.repeat.setOnClickListener {
            if (isRepeatOn) {
                isRepeatOn = false
                binding.repeat.setImageResource(R.drawable.repeat_off)
            } else {
                isRepeatOn = true
                binding.repeat.setImageResource(R.drawable.repeat_on)
            }
        }


        setContentView(binding.root)
    }

    fun formattedTime(mCurrentPosition: Int): String {
        var totalout = ""
        var totalNew = ""
        var seconds = (mCurrentPosition % 60).toString()
        var minutes = (mCurrentPosition / 60).toString()
        totalout = "$minutes:$seconds"
        totalNew = "$minutes:0$seconds"
        return if (seconds.length == 1) {
            totalNew
        } else {
            totalout
        }
    }

    fun getIntentMethod() {
        position = intent.getIntExtra("position", -1)
        var albumSongs = intent.getSerializableExtra("list") as? ArrayList<MusicFiles>
        var sender = intent.getStringExtra("sender")
        if (sender != null && sender.equals("albumDetails") && albumSongs != null) {
            listSongs = albumSongs
        } else {
            listSongs = getList()
        }


        if (listSongs != null) {
            binding.playPause.setImageResource(R.drawable.pause_ic)
            uri = Uri.parse(listSongs.get(position).path)
        }

//        if (musicService != null){
//            musicService.stop()
//            musicService.release()
//        }
//        musicService.createMediaPLayer(position)
//        musicService.start()


        showNotification(R.drawable.pause_ic)
        var intent = Intent(this, MusicService::class.java)
        intent.putExtra("servicePosition", position)
        startService(intent)
//        binding.seekbar.max = musicService.getDuration() / 1000
//        metaData(uri)
    }


    fun metaData(uri: Uri) {
        var retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        var durationTotal = Integer.parseInt(listSongs.get(position).duration) / 1000
        binding.durationTotal.text = formattedTime(durationTotal)
        var art: ByteArray? = retriever.embeddedPicture

        if (art != null) {
            var bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
            imageAnimation(this, binding.coverArt, bitmap)
            Palette.from(bitmap).generate(object : Palette.PaletteAsyncListener {
                override fun onGenerated(p0: Palette?) {
                    if (p0 != null) {
                        var swatch = p0.dominantSwatch
                        var gradient = binding.imageViewGradient
                        var mContainer = binding.mContainer
                        gradient.setBackgroundColor(R.drawable.gradient_bg)
                        mContainer.setBackgroundResource(R.drawable.main_bg)
                        var gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(swatch!!.rgb, 0x00000000)
                        )
                        gradient.background = gradientDrawable
                        var gradientDrawableBg = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(swatch!!.rgb, swatch!!.rgb)
                        )
                        mContainer.background = gradientDrawableBg
                        binding.songName.setTextColor(swatch.titleTextColor)
                        binding.songArtist.setTextColor(swatch.bodyTextColor)

                    } else {
                        var gradient = binding.imageViewGradient
                        var mContainer = binding.mContainer
                        gradient.setBackgroundColor(R.drawable.gradient_bg)
                        mContainer.setBackgroundResource(R.drawable.main_bg)
                        var gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(0xff000000.toInt(), 0x00000000)
                        )
                        gradient.background = gradientDrawable
                        var gradientDrawableBg = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(0xff000000.toInt(), 0xff000000.toInt())
                        )
                        mContainer.background = gradientDrawableBg
                        binding.songName.setTextColor(Color.WHITE)
                        binding.songArtist.setTextColor(Color.DKGRAY)

                    }
                }

            })
        } else {
            if (!isDestroyed) {
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
        var intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        playThreadBtn()
        nextThreadBtn()
        prevThreadBtn()
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }

    private fun prevThreadBtn() {
        var prevThread = Thread {
            binding.previous.setOnClickListener {
                prevBtnClicked()
            }
        }
        prevThread.start()

    }


    private fun nextThreadBtn() {
        var nextThread = Thread {
            binding.next.setOnClickListener {
                nextBtnClicked()
            }
        }
        nextThread.start()

    }


    private fun playThreadBtn() {
        var playThread = Thread {
            binding.playPause.setOnClickListener {
                playPauseBtnClicked()
            }
        }
        playThread.start()

    }

    override fun playPauseBtnClicked() {
        if (musicService.isPlaying()) {
            binding.playPause.setImageResource(R.drawable.play_ic)
            showNotification(R.drawable.play_ic)
            musicService.pause()
            binding.seekbar.max = musicService.getDuration() / 1000
            this.runOnUiThread(object : Runnable {
                override fun run() {
                    if (musicService != null) {
                        var mCurrentPosition = (musicService.getCurrentPosition() / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this, 1000)
                }

            })
        } else {
            showNotification(R.drawable.pause_ic)
            binding.playPause.setImageResource(R.drawable.pause_ic)
            musicService.start()
            binding.seekbar.max = musicService.getDuration() / 1000
            this.runOnUiThread(object : Runnable {
                override fun run() {
                    if (musicService != null) {
                        var mCurrentPosition = (musicService.getCurrentPosition() / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this, 1000)
                }

            })
        }
    }

    override fun nextBtnClicked() {


        if (musicService.isPlaying()) {
            musicService.stop()
            musicService.release()

            if (isShuffleOn && !isRepeatOn) {
                position = getRandom(listSongs.size - 1)
            } else if (!isShuffleOn && !isRepeatOn) {
                position = ((position + 1) % listSongs.size)
            }
            uri = Uri.parse(listSongs.get(position).path)
            musicService.createMediaPLayer(position)
            metaData(uri)
            binding.songName.text = listSongs.get(position).title
            binding.songArtist.text = listSongs.get(position).artist
            binding.seekbar.max = musicService.getDuration() / 1000
            this.runOnUiThread(object : Runnable {
                override fun run() {
                    if (musicService != null) {
                        var mCurrentPosition = (musicService.getCurrentPosition() / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this, 1000)
                }

            })
            musicService.onCompleted()
            showNotification(R.drawable.pause_ic)
            binding.playPause.setBackgroundResource(R.drawable.pause_ic)
            musicService.start()
        } else {
            musicService.stop()
            musicService.release()
            if (isShuffleOn && !isRepeatOn) {
                position = getRandom(listSongs.size - 1)
            } else if (!isShuffleOn && !isRepeatOn) {
                position = ((position + 1) % listSongs.size)
            }
            uri = Uri.parse(listSongs.get(position).path)
            musicService.createMediaPLayer(position)
            metaData(uri)
            binding.songName.text = listSongs.get(position).title
            binding.songArtist.text = listSongs.get(position).artist
            binding.seekbar.max = musicService.getDuration() / 1000
            this.runOnUiThread(object : Runnable {
                override fun run() {
                    if (musicService != null) {
                        var mCurrentPosition = (musicService.getCurrentPosition() / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this, 1000)
                }

            })
            musicService.onCompleted()

            showNotification(R.drawable.play_ic)
            binding.playPause.setBackgroundResource(R.drawable.play_ic)
        }
    }


    override fun prevBtnClicked() {
        if (musicService.isPlaying()) {
            musicService.stop()
            musicService.release()
            if (isShuffleOn && !isRepeatOn) {
                position = getRandom(listSongs.size - 1)
            } else if (!isShuffleOn && !isRepeatOn) {
                position = if (position - 1 < 0) listSongs.size - 1 else position - 1
            }
            uri = Uri.parse(listSongs.get(position).path)
            musicService.createMediaPLayer(position)
            metaData(uri)
            binding.songName.text = listSongs.get(position).title
            binding.songArtist.text = listSongs.get(position).artist
            binding.seekbar.max = musicService.getDuration() / 1000
            this.runOnUiThread(object : Runnable {
                override fun run() {
                    if (musicService != null) {
                        var mCurrentPosition = (musicService.getCurrentPosition() / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this, 1000)
                }

            })
            musicService.onCompleted()
            showNotification(R.drawable.pause_ic)
            binding.playPause.setBackgroundResource(R.drawable.pause_ic)
            musicService.start()
        } else {
            musicService.stop()
            musicService.release()
            if (isShuffleOn && !isRepeatOn) {
                position = getRandom(listSongs.size - 1)
            } else if (!isShuffleOn && !isRepeatOn) {
                position = if (position - 1 < 0) listSongs.size - 1 else position - 1
            }
            uri = Uri.parse(listSongs.get(position).path)
            musicService.createMediaPLayer(position)
            metaData(uri)
            binding.songName.text = listSongs.get(position).title
            binding.songArtist.text = listSongs.get(position).artist
            binding.seekbar.max = musicService.getDuration() / 1000
            this.runOnUiThread(object : Runnable {
                override fun run() {
                    if (musicService != null) {
                        var mCurrentPosition = (musicService.getCurrentPosition() / 1000)
                        binding.seekbar.progress = mCurrentPosition
                    }
                    Handler().postDelayed(this, 1000)
                }

            })
            musicService.onCompleted()
            showNotification(R.drawable.play_ic)
            binding.playPause.setBackgroundResource(R.drawable.play_ic)
        }
    }

    private fun getRandom(i: Int): Int {
        return Random.nextInt(i + 1)
    }

    fun imageAnimation(context: Context, imageView: ImageView, bitmap: Bitmap) {
        var animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        var animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                Glide.with(context).load(bitmap).into(imageView)
                animIn.setAnimationListener(object : Animation.AnimationListener {
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


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        var myBinder: MusicService.MyBinder = service as MusicService.MyBinder
        musicService = myBinder.getService()
        musicService.setCallBack(this)
        Toast.makeText(this, "Connected $musicService", Toast.LENGTH_SHORT).show()
        binding.seekbar.max = musicService.getDuration() / 1000
        metaData(uri)
        musicService.onCompleted()
        binding.songName.text = listSongs.get(position).title
        binding.songArtist.text = listSongs.get(position).title
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }

    fun showNotification(playPauseBtn: Int) {

        val notificationId = listSongs[position].path.hashCode()

        var intent = Intent(this, PlayerActivity::class.java)
        var contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        var prevIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(ACTION_PREVIOUS)
        var prevPending = PendingIntent.getBroadcast(
            this,
            0,
            prevIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        var pauseIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_PLAY)
        var pausePending =
            PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE)


        var nextIntent = Intent(this, NotificationReceiver::class.java)
            .setAction(ACTION_NEXT)
        var nextPending = PendingIntent.getBroadcast(
            this,
            0,
            nextIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        var picture: ByteArray? = null
        picture = getAlbumArt(listSongs.get(position).path.toUri())

        var thumb: Bitmap? = null
        if (picture != null) {
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.size)
        } else {
            thumb = BitmapFactory.decodeResource(resources, R.drawable.defaultimg)
        }
        var notification = NotificationCompat.Builder(this, CHANNEL_ID_2)
            .setSmallIcon(playPauseBtn)
            .setLargeIcon(thumb)
            .setContentTitle(listSongs[position].title)
            .setContentText(listSongs[position].artist)
            .addAction(R.drawable.skip_previous, "Previous", prevPending)
            .addAction(playPauseBtn, "Pause", pausePending)
            .addAction(R.drawable.skip_next, "Next", nextPending)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .build()

        var notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun getAlbumArt(uri: Uri): ByteArray? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(uri.toString())
            val art: ByteArray? = retriever.embeddedPicture
            return art
        } catch (e: Exception) {
            // Handle the exception, such as logging or displaying an error message.
            e.printStackTrace()
        } finally {
            retriever.release()
        }
        return null
    }


}