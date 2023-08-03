package com.muradakhundov.musicplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muradakhundov.musicplayer.MainActivity
import com.muradakhundov.musicplayer.MainActivity.Companion.musicFiles
import com.muradakhundov.musicplayer.R
import com.muradakhundov.musicplayer.adapter.MusicAdapter
import com.muradakhundov.musicplayer.databinding.FragmentSongsBinding


class SongsFragment : Fragment() {
    private lateinit var binding : FragmentSongsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSongsBinding.inflate(layoutInflater,container,false)

        binding.recyclerView.setHasFixedSize(true)
        if (!(musicFiles.size < 1)){
            var musicAdapter = MusicAdapter(requireContext(), musicFiles)
            binding.recyclerView.adapter = musicAdapter
            binding.recyclerView.smoothScrollToPosition(0)
        }
        return binding.root
    }


}