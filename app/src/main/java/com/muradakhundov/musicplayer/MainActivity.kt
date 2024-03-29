package com.muradakhundov.musicplayer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.muradakhundov.musicplayer.adapter.MusicAdapter
import com.muradakhundov.musicplayer.adapter.ViewPagerAdapter
import com.muradakhundov.musicplayer.databinding.ActivityMainBinding
import com.muradakhundov.musicplayer.fragments.SongsFragment.Companion.musicAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaSession: MediaSessionCompat


    companion object {
        private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1
        var isShuffleOn = false
        var isRepeatOn = false
        var musicFiles : ArrayList<MusicFiles> = ArrayList()
        var albums : ArrayList<MusicFiles> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val adapter = ViewPagerAdapter(this)
        binding.viewpager.adapter = adapter

        mediaSession = MediaSessionCompat(this, "MusicService")

        permission()
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = "Songs"
                1 -> tab.text = "Albums"
            }
        }.attach()


        setUpSearchView()
        setContentView(binding.root)
    }


    fun setUpSearchView(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var myFiles : ArrayList<MusicFiles> = ArrayList()
                if (newText!=null){
                    var userInput = newText.toLowerCase()

                    for (song in musicFiles){
                        if (song.title.toLowerCase().contains(userInput)){
                            myFiles.add(song)
                            MusicAdapter.setStaticValues(applicationContext,myFiles)
                        }
                    }

                }
                musicAdapter.updateList(myFiles)
                return true

            }

        })
    }

    fun permission() {
        val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission),
                WRITE_EXTERNAL_STORAGE_REQUEST_CODE
            )
        } else {
            // Permission already granted
            musicFiles = getAllAudio(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                musicFiles = getAllAudio(this)
            } else {
                // Permission denied
                // Handle the situation when permission is denied
            }
        }
    }


    fun getAllAudio(context : Context) : ArrayList<MusicFiles> {
        var duplicate : ArrayList<String> = ArrayList()
        var tempAudioList = ArrayList<MusicFiles>()
        var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )

        var cursor = context.contentResolver.query(uri,projection,null,null,null)
        if (cursor != null){
            while (cursor.moveToNext()){
                var album = cursor.getString(0)
                var title = cursor.getString(1)
                var duration = cursor.getString(2) ?: "Unknown Duration"
                var path = cursor.getString(3)
                var artist = cursor.getString(4)
                var id = cursor.getString(5)

                var musicFiles = MusicFiles(path,title,artist,album, duration,id)
                tempAudioList.add(musicFiles)
                if (!duplicate.contains(album)){
                    albums.add(musicFiles)
                    duplicate.add(album)
                }
            }
        }
        return tempAudioList
    }
}