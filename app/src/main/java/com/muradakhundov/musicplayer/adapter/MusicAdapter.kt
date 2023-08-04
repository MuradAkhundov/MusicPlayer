package com.muradakhundov.musicplayer.adapter

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muradakhundov.musicplayer.MusicFiles
import com.muradakhundov.musicplayer.PlayerActivity
import com.muradakhundov.musicplayer.R
import com.muradakhundov.musicplayer.databinding.MusicItemsBinding
import com.muradakhundov.musicplayer.fragments.SongsFragment
import kotlinx.coroutines.launch
import java.io.File

class MusicAdapter(var mContext: Context, var musicList: ArrayList<MusicFiles>) :
    RecyclerView.Adapter<MusicAdapter.MusicDesignHolder>() {

    companion object{
        private lateinit var staticMusicList : ArrayList<MusicFiles>
        private lateinit var staticContext : Context
        fun setStaticValues(context: Context , list: ArrayList<MusicFiles>){
            staticContext = context
            staticMusicList = list
        }
        fun getList() : ArrayList<MusicFiles> = staticMusicList
        fun getContext() : Context = staticContext
    }

    inner class MusicDesignHolder(val binding: MusicItemsBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        } else {
            Glide.with(mContext).asBitmap()
                .load(R.drawable.defaultimg)
                .into(b.imageMusic)
        }

        b.root.setOnClickListener {
            var intent = Intent(mContext, PlayerActivity::class.java)
            intent.putExtra("position", position)
            mContext.startActivity(intent)
        }

        b.moreMenu.setOnClickListener {
            var popupMenu = PopupMenu(mContext, it)
            popupMenu.menuInflater.inflate(R.menu.popup, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> deleteFile(position, it)
                    else -> {
                        // Handle other menu item clicks, if necessary.
                        // If you don't need to do anything for other menu items, you can omit this block.
                    }
                }
                true // Return true to indicate that the click event has been handled.
            }


        }


    }

    fun deleteFile(position: Int, v: View) {
        val music = musicList[position]
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            music.id.toLong()
        )
        val file = File(music.path)

        try {
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    mContext.contentResolver.delete(contentUri, null, null)
                    musicList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, musicList.size)
                    Toast.makeText(mContext, "File Deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("DeleteFile", "File couldn't be deleted: ${file.absolutePath}")
                    Toast.makeText(mContext, "Cannot Delete", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("DeleteFile", "File not found: ${file.absolutePath}")
                Toast.makeText(mContext, "File not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("DeleteFile", "Error deleting file: ${e.message}")
            Toast.makeText(mContext, "Error deleting file", Toast.LENGTH_SHORT).show()
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

    fun updateList(musicFilesArr : ArrayList<MusicFiles>){
        musicList = ArrayList()
        musicList.addAll(musicFilesArr)
        notifyDataSetChanged()
    }
}