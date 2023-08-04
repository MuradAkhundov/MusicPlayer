package com.muradakhundov.musicplayer.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass : Application() {

    companion object{
        val CHANNEL_ID_1 = "channel1"
        val CHANNEL_ID_2 = "channel2"
        val ACTION_PREVIOUS = "actionprevious"
        val ACTION_NEXT = "actionnext"
        val ACTION_PLAY = "actionplay"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel1 = NotificationChannel(CHANNEL_ID_1, "Channel(1)",NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "Channel 1 Desc..."

            val channel2 = NotificationChannel(CHANNEL_ID_2, "Channel(1)",NotificationManager.IMPORTANCE_HIGH)
            channel2.description = "Channel 2 Desc..."

            var notificationManager : NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel1)
            notificationManager.createNotificationChannel(channel2)

        }
    }
}