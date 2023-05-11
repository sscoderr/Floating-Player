package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Result;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.ResultForPlaylist;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("api/search")
    Call<Result> getResult(@Query("q") String q, @Query("only") String only);
    @GET("api/search")
    Call<Result> getResult(@Query("q") String q,@Query("page") String page, @Query("only") String only);
    @GET("api/search")
    Call<ResultForPlaylist> getResultForPlaylist(@Query("q") String q, @Query("page") String page, @Query("only") String only);
}