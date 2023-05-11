package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.SimilarVideos;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("api/similar-videos")
    Call<Result> getResult(@Query("videoId") String videoId);
}