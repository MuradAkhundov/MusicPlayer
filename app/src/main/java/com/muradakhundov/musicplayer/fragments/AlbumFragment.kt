package com.muradakhundov.musicplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.muradakhundov.musicplayer.MainActivity
import com.muradakhundov.musicplayer.R
import com.muradakhundov.musicplayer.adapter.AlbumAdapter
import com.muradakhundov.musicplayer.adapter.MusicAdapter
import com.muradakhundov.musicplayer.databinding.FragmentAlbumBinding

class AlbumFragment : Fragment() {
    private lateinit var binding : FragmentAlbumBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentAlbumBinding.inflate(layoutInflater,container,false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = GridLayoutManager(context,2)
        if (!(MainActivity.albums.size < 1)){
            var albumAdapter = AlbumAdapter(requireContext(), MainActivity.albums)
            binding.recyclerView.adapter = albumAdapter
            binding.recyclerView.smoothScrollToPosition(0)
        }
        return binding.root
    }


}