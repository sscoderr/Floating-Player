package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Playlist;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("api/playlist")
    Call<Result> getResult(@Query("playlistId") String playlistId);


}
