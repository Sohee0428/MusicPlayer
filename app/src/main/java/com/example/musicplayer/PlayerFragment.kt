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
    private lateinit var playListAdapter: PlayListAdapter
    private var player: SimpleExoPlayer? = null
    private var model: PlayerModel = PlayerModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initPlayView(fragmentPlayerBinding)
        initPlayLIstBtn(fragmentPlayerBinding)
        initPlayControlBtn(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)

        getVideoListFromServer()

    }

    private fun initPlayControlBtn(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playControlImg.setOnClickListener{
            val player = this.player ?: return@setOnClickListener

            if (player.isPlaying){
                player.pause()
            } else {
                player.play()
            }
        }

        fragmentPlayerBinding.skipNextImg.setOnClickListener {

        }

        fragmentPlayerBinding.skipPrevImg.setOnClickListener {

        }
    }

    private fun initPlayView(fragmentPlayerBinding: FragmentPlayerBinding) {
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }

        fragmentPlayerBinding.playerView.player = player

        binding?.let {

            player?.addListener(object : Player.EventListener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    if(isPlaying) {
                        it.playControlImg.setImageResource(R.drawable.ic_baseline_pause_24)
                    } else {
                        it.playControlImg.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }
            })
        }
    }

    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {
        playListAdapter = PlayListAdapter {
//            todo 음악을 재생
        }
        fragmentPlayerBinding.playListRecyclerView.apply {
            adapter = playListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayLIstBtn(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playListImg.setOnClickListener{

            if (model.currentPosition == -1) return@setOnClickListener

            fragmentPlayerBinding.playerViewGroup.isVisible = model.isWatchingPlayListView
            fragmentPlayerBinding.playerListViewGroup.isVisible = !model.isWatchingPlayListView

            model.isWatchingPlayListView = !model.isWatchingPlayListView
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

                            response.body()?.let {  musicDto ->

                                model = musicDto.mapper()

                                setMusicList(model.getAdapterModels())
                                playListAdapter.submitList(model.getAdapterModels())
                            }
                        }

                        override fun onFailure(call: Call<MusicDto>, t: Throwable) {
                            Log.d("PlayerFragment", "실패")
                        }
                    })
            }
    }

    private fun setMusicList(modelList: List<MusicModel>) {
        context?.let {
            player?.addMediaItems(modelList.map { musicModel ->
                MediaItem.Builder()
                    .setMediaId(musicModel.id.toString())
                    .setUri(musicModel.streamUrl)
                    .build()
            })

            player?.prepare()
        }
    }

}