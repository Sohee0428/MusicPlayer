package com.example.musicplayer.service

import retrofit2.http.GET

interface MusicService {

    @GET("/v3/9bb37621-97db-413c-8210-4ef87e6730e8")
    fun listMusics(): retrofit2.Call<MusicDto>
}