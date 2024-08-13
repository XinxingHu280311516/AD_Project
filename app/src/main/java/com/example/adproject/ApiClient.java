package com.example.adproject;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.CookieJar;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.1.59:8080/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // 创建CookieJar以管理Session
            CookieJar cookieJar = new CookieJar() {
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<>();
                }
            };

            // 创建OkHttpClient并设置CookieJar
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .connectTimeout(300, java.util.concurrent.TimeUnit.SECONDS) // 连接超时
                    .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)    // 读取超时
                    .writeTimeout(300, java.util.concurrent.TimeUnit.SECONDS)   // 写入超时
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();

            // 将OkHttpClient传入Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}
