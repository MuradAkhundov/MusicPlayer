package com.muradakhundov.musicplayer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.muradakhundov.musicplayer.adapter.ViewPagerAdapter
import com.muradakhundov.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding



    companion object {
        private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1
        var musicFiles : ArrayList<MusicFiles> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        Log.e("tag", "onCreate is called")
        val adapter = ViewPagerAdapter(this)
        binding.viewpager.adapter = adapter


        permission()
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = "Songs"
                1 -> tab.text = "Albums"
            }
        }.attach()


        setContentView(binding.root)
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
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                musicFiles = getAllAudio(this)
            } else {
                // Permission denied
                Toast.makeText(this, "Permission Denied. The app requires storage permission to function.", Toast.LENGTH_SHORT).show()
                // Handle the situation when permission is denied
            }
        }
    }


    fun getAllAudio(context : Context) : ArrayList<MusicFiles> {
        var tempAudioList = ArrayList<MusicFiles>()
        var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
        )

        var cursor = context.contentResolver.query(uri,projection,null,null,null)
        if (cursor != null){
            while (cursor.moveToNext()){
                var album = cursor.getString(0)
                var title = cursor.getString(1)
                var duration = cursor.getString(2) ?: "Unknown Duration"
                var path = cursor.getString(3)
                var artist = cursor.getString(4)

                var musicFiles = MusicFiles(path,title,artist,album, duration)
                tempAudioList.add(musicFiles)
            }
        }
        return tempAudioList
    }
}