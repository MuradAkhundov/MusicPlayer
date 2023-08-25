package com.muradakhundov.musicplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.muradakhundov.musicplayer.service.ApplicationClass.Companion.ACTION_NEXT
import com.muradakhundov.musicplayer.service.ApplicationClass.Companion.ACTION_PLAY
import com.muradakhundov.musicplayer.service.ApplicationClass.Companion.ACTION_PREVIOUS
import com.muradakhundov.musicplayer.service.MusicService

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var actionName = intent!!.action
        var serviceIntent = Intent(context,MusicService::class.java)
        if (actionName != null){
            when(actionName){
                ACTION_PLAY ->{
                    serviceIntent.putExtra("actionName","playPause")
                    context?.startService(serviceIntent)
                }
                ACTION_NEXT ->{
                    serviceIntent.putExtra("actionName","next")
                    context?.startService(serviceIntent)
                }
                ACTION_PREVIOUS ->{
                    serviceIntent.putExtra("actionName","previous")
                    context?.startService(serviceIntent)
                }

            }
        }
    }
}