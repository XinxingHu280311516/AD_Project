package com.example.adproject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("login")
    Call<User> login(@Body User user);

    @POST("register")
    Call<String> register(@Body User user);
}
