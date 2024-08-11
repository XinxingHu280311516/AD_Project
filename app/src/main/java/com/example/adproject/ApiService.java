package com.example.adproject;

import java.util.List;
import java.util.Map;

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

public interface ApiService {
    @POST("api/login")
    Call<User> login(@Body User user);

    @POST("api/register")
    Call<String> register(@Body User user);

    @Multipart
    @POST("upload")
    Call<String> uploadImage(@Part MultipartBody.Part file, @Part("description") RequestBody description);

    @GET("User/budget/{userId}")
    Call<List<Category>> getUserCategories(@Path("userId") int userId);

    //@GET("categories/{userId}")
    //Call<List<String>> getUserCategories(@Path("userId") int userId);

    @POST("User/transaction/add/{userId}")
    Call<Transaction> addTransaction(@Body Transaction transaction, @Path("userId") int userId);

    @POST("User/budget/add/{userId}")
    Call<Category> addCategory(@Body Category category, @Path("userId") int userId);

    @PUT("User/budget/update/{catId}")
    Call<Category> updateCategory(@Body Category category, @Path("catId") int catId);

    @GET("User/budget/{userId}")
    Call<Double> getUserCurrentMouth(@Path("userId") int userId);

//    @GET("User/category-spending/{userId}")
//    Call<List<Object[]>> getTotalSpendingByCategoryForCurrentMonth(@Path("userId") Integer userId);

    @DELETE("User/budget/delete/{catId}")
    Call<Void> deleteCategory(@Path("catId") int catId);

    @GET("/Admin/transaction_user/{userId}")
    Call<List<Transaction>> getTransactionByUserId(@Path("userId") Integer userId);

    @GET("User/category/{id}")
    Call<Category> getCategoryById(@Path("id") Integer id);

    @DELETE("/User/transaction/delete/{transId}")
    Call<Void> deleteTransaction(@Path("transId") Integer id);

    @PUT("User/transaction/update/{transId}")
    Call<Transaction> updateTransaction(@Body Transaction transaction, @Path("transId") Integer id);

    @GET("Admin/categories/{type}")
    Call<List<Category>> getCategoriesByType(@Path("type") int type);

    @GET("User/categories/total-budget/{userId}")
    Call<Double> getTotalBudgetByUserId(@Path("userId") Integer userId);

    @GET("User/total-spending-current/{userId}")
    Call<Double> getTotalSpendingCurrent(@Path("userId") Integer userId);

    @GET("User/total-spending-current-mouth-by-category/{userId}")
    Call<List<Map<String, Object>>> getTotalSpendingByCategoryForCurrentMonth(@Path("userId") Integer userId);

    @GET("/jky/Tips/{userId}")
    Call<List<Map<String, String>>> getBudgetAdjustmentTips(@Path("userId") int userId);
}
