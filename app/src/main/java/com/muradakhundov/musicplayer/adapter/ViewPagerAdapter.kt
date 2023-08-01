package com.muradakhundov.musicplayer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.muradakhundov.musicplayer.fragments.AlbumFragment
import com.muradakhundov.musicplayer.fragments.SongsFragment
import java.lang.IllegalArgumentException

class ViewPagerAdapter(fa : FragmentActivity) : FragmentStateAdapter(fa)
 {
     companion object{
         private const val NUM_PAGES = 2
     }

     override fun getItemCount(): Int {
         return NUM_PAGES
     }

     override fun createFragment(position: Int): Fragment {
         return when(position){
             0 -> SongsFragment()
             1 -> AlbumFragment()
             else -> throw IllegalArgumentException("Invalid position")
         }
     }
 }