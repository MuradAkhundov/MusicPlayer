package com.muradakhundov.musicplayer

import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.muradakhundov.musicplayer.MainActivity.Companion.musicFiles
import com.muradakhundov.musicplayer.adapter.AlbumDetailsAdapter
import com.muradakhundov.musicplayer.databinding.ActivityAlbumDetailsBinding

class AlbumDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAlbumDetailsBinding
    var albumSongs = ArrayList<MusicFiles>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =   ActivityAlbumDetailsBinding.inflate(layoutInflater)
        var albumName = intent.getStringExtra("albumName")
        var j = 0
        for (i in 0 until musicFiles.size ){
            if (albumName != null){
                if (albumName.equals(musicFiles.get(i).album)){
                    albumSongs.add(j, musicFiles.get(i))
                    j ++
                }
            }
        }

        var image : ByteArray? = getAlbumArt(albumSongs.get(0).path)
        if (image != null){
            Glide.with(this)
                .load(image)
                .into(binding.albumPhoto)
        }
        else{
            Glide.with(this)
                .load(R.drawable.defaultimg)
                .into(binding.albumPhoto)


        }
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if (albumSongs.size >= 1){
            var albumDetailsAdapter = AlbumDetailsAdapter(this,albumSongs)
            binding.recyclerView.adapter = albumDetailsAdapter
        }

    }
    fun getAlbumArt(uri : String) : ByteArray?{
       var mMR =  MediaMetadataRetriever()
       mMR.setDataSource(uri)
       var art = mMR.embeddedPicture
       mMR.release()
       return art
    }
}