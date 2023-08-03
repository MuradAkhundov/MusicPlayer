package com.muradakhundov.musicplayer.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muradakhundov.musicplayer.AlbumDetailsActivity
import com.muradakhundov.musicplayer.MusicFiles
import com.muradakhundov.musicplayer.PlayerActivity
import com.muradakhundov.musicplayer.R
import com.muradakhundov.musicplayer.databinding.AlbomItemBinding
import com.muradakhundov.musicplayer.databinding.MusicItemsBinding

class AlbumDetailsAdapter(val mContext : Context, var albomList : ArrayList<MusicFiles>) : RecyclerView.Adapter<AlbumDetailsAdapter.AlbumViewHolder>()  {

    inner class AlbumViewHolder(val binding : MusicItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = MusicItemsBinding.inflate(LayoutInflater.from(mContext))
        return AlbumViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return albomList.size
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val b = holder.binding
        val list = albomList.get(position)
        var myList = albomList
        b.nameMusic.text = list.title
        val image = getAlbumArt(list.path.toUri())

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
            var intent = Intent(mContext, PlayerActivity::class.java)
            intent.putExtra("sender", "albumDetails")
            intent.putExtra("position",position)
            intent.putExtra("list",albomList)
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
    }


}