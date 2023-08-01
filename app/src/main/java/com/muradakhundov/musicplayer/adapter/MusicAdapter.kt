package com.muradakhundov.musicplayer.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muradakhundov.musicplayer.MusicFiles
import com.muradakhundov.musicplayer.PlayerActivity
import com.muradakhundov.musicplayer.R
import com.muradakhundov.musicplayer.databinding.MusicItemsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MusicAdapter(var mContext : Context, var musicList : ArrayList<MusicFiles>) : RecyclerView.Adapter<MusicAdapter.MusicDesignHolder>() {
    private val albumArtCache: MutableMap<Uri, ByteArray?> = HashMap()
    inner class MusicDesignHolder(val binding: MusicItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicDesignHolder {
        val binding = MusicItemsBinding.inflate(LayoutInflater.from(mContext))
        return MusicDesignHolder(binding)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: MusicDesignHolder, position: Int) {
        val b = holder.binding
        val myList = musicList.get(position)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             val image = getAlbumArt(myList.path.toUri())


        b.nameMusic.text = myList.title

          if (image != null) {
              Glide.with(mContext).asBitmap()
                  .load(image)
                  .into(b.imageMusic)
          }
        else{
              Glide.with(mContext).asBitmap()
                  .load(R.drawable.defaultimg)
                  .into(b.imageMusic)
          }

        b.root.setOnClickListener {
            var intent = Intent(mContext,PlayerActivity::class.java)
            intent.putExtra("position",position)
            mContext.startActivity(intent)
        }



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
    }    }