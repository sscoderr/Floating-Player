package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET(".")
    Call<String> getStringResponse();
}
