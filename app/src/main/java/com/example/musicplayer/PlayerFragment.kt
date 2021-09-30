package com.example.musicplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.FragmentPlayerBinding
import com.example.musicplayer.service.MusicDto
import com.example.musicplayer.service.MusicService
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlayerFragment: Fragment(R.layout.fragment_player) {

    private var binding: FragmentPlayerBinding? = null
    private var isWatchingPlayListView = true
    private lateinit var playListAdapter: PlayListAdapter
    private var player: SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initPlayLIstBtn(fragmentPlayerBinding)


        getVideoListFromServer()

    }



    private fun initPlayLIstBtn(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playListImg.setOnClickListener{
//            todo 만약에 서버에서 데이터가 다 불려오지 않았을 때

            fragmentPlayerBinding.playerViewGroup.isVisible = isWatchingPlayListView
            fragmentPlayerBinding.playerListViewGroup.isVisible = !isWatchingPlayListView

            isWatchingPlayListView = !isWatchingPlayListView
        }
    }

    private fun getVideoListFromServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MusicService::class.java)
            .also {
                it.listMusics()
                    .enqueue(object : Callback<MusicDto>{
                        override fun onResponse(
                            call: Call<MusicDto>,
                            response: Response<MusicDto>
                        ) {
                            Log.d("PlayerFragment", "${response.body()}")

                            response.body()?.let {
                                val modelList = it.musics.mapIndexed { index, musicEntity ->
                                    musicEntity.mapper(index.toLong())
                                }


                                playListAdapter.submitList(modelList)
                            }
                        }

                        override fun onFailure(call: Call<MusicDto>, t: Throwable) {
                            Log.d("PlayerFragment", "실패")
                        }
                    })
            }
    }


}