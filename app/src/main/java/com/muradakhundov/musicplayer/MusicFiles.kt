package com.muradakhundov.musicplayer

import java.io.Serializable

data class MusicFiles(
    var path: String,
    var title: String,
    var artist : String,
    var album : String,
    var duration : String
) : Serializable
