package com.example.adproject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("login")
    Call<User> login(@Body User user);

    @POST("register")
    Call<String> register(@Body User user);

    @Multipart
    @POST("upload")
    Call<String> uploadImage(@Part MultipartBody.Part file, @Part("description") RequestBody description);

    @GET("categories/{userId}")
    Call<List<Category>> getUserCategories(@Path("userId") int userId);

    //@GET("categories/{userId}")
    //Call<List<String>> getUserCategories(@Path("userId") int userId);

    @POST("transaction/add/{userId}")
    Call<Transaction> addTransaction(@Body Transaction transaction, @Path("userId") int userId);

    @POST("budget/add/{userId}")
    Call<Category> addCategory(@Body Category category, @Path("userId") int userId);

    @PUT("budget/update/{catId}")
    Call<Category> updateCategory(@Body Category category, @Path("catId") int catId);

    @DELETE("budget/delete/{catId}")
    Call<Void> deleteCategory(@Path("catId") int catId);

    @GET("/Admin/transaction_user/{userId}")
    Call<List<Transaction>> getTransactionByUserId(@Path("userId") Integer userId);

    @DELETE("/Admin/delete/{id}")
    Call<Void> deleteTransaction(@Path("id") Integer id);

    @PUT("/Admin/update/{id}")
    Call<Transaction> updateTransaction(@Path("id") Integer id, @Body Transaction transaction);
}
