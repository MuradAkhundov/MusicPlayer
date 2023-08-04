package com.muradakhundov.musicplayer.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muradakhundov.musicplayer.MainActivity
import com.muradakhundov.musicplayer.MainActivity.Companion.musicFiles
import com.muradakhundov.musicplayer.MusicFiles
import com.muradakhundov.musicplayer.adapter.MusicAdapter
import com.muradakhundov.musicplayer.databinding.FragmentSongsBinding


class SongsFragment : Fragment() {
    private lateinit var binding : FragmentSongsBinding
    companion object{
        lateinit var musicAdapter : MusicAdapter
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()

        var emptyArrayList : ArrayList<MusicFiles> = ArrayList()
        musicAdapter = MusicAdapter(requireContext(),emptyArrayList )
        // Inflate the layout for this fragment
        binding = FragmentSongsBinding.inflate(layoutInflater,container,false)
        binding.recyclerView.setHasFixedSize(true)
        if (!(musicFiles.size < 1)){
            musicAdapter = MusicAdapter(requireContext(), musicFiles)
            MusicAdapter.setStaticValues(context, musicFiles)
            binding.recyclerView.adapter = musicAdapter
            binding.recyclerView.smoothScrollToPosition(0)
        }
        return binding.root
    }


}