package com.example.adproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private ApiService apiService;
    private int userId;
    List<Category> allCategories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        apiService = ApiClient.getApiService();
        SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
        userId = pref.getInt("UserId", -1);

        saveCategoryImagePath("Food", R.drawable.image6);
        saveCategoryImagePath("Drinking", R.drawable.image7);
        saveCategoryImagePath("House", R.drawable.image8);

        allCategories = new ArrayList<>();
        loadAllCategories();


        Button logoutButton = findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }

//                SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.clear();
//                editor.apply();
//
//                // 跳转到登录界面
//                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
////                finish(); // 结束当前Activity
//            }
        });


        Button captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TextRecognitionActivity.class);
                startActivity(intent);
                // Handle capture button click
            }
        });

        Button viewReportButton = findViewById(R.id.view_report_button);
        viewReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle view report button click
                Intent intent = new Intent(HomeActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

        Button viewButton = findViewById(R.id.view_button);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle view button click
                Intent intent = new Intent(HomeActivity.this, ExpenseReportActivity.class);
                startActivity(intent);
            }
        });

        Button viewByDateButton = findViewById(R.id.view_by_date_button);
        viewByDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle view by date button click
                Intent intent = new Intent(HomeActivity.this, ViewByDateActivity.class);
                startActivity(intent);
            }
        });

        Button budgetButton = findViewById(R.id.budget_button);
        budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BudgetPlanActivity.class);
                startActivity(intent);
            }
        });

        Button financialButton = findViewById(R.id.financial_button);
        financialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle financial button click
                Intent intent = new Intent(HomeActivity.this, PersonalizedRecommendationsActivity.class);
                startActivity(intent);
            }
        });

        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EnterExpenseActivity.class);
                startActivity(intent);
            }
        });

    }

    private void logout() {
        SharedPreferences pref = getSharedPreferences("user_session", MODE_PRIVATE);
        String sessionId = pref.getString("user_session", null);

        if (sessionId != null) {
            // 构建请求头，包含用户的会话Cookie
            Map<String, String> headers = new HashMap<>();
            headers.put("Cookie", sessionId);

            Call<Void> call = apiService.logout(headers);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // 清除本地存储的Session数据
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.apply();

                        // 跳转到登录界面
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // 结束当前Activity
                    } else {
                        Toast.makeText(HomeActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 如果Session ID不存在，直接跳转到登录界面
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private List<Category> filterCategoriesByUserAndType(List<Category> allCategories,int userId) {
        List<Category> filteredCategories = new ArrayList<>();
        for (Category category : allCategories) {
            if (category.getUser().getId() == userId && category.getType() == 0) {
                filteredCategories.add(category);
            }
        }
        return filteredCategories;
    }

    private void loadCategoryImages(List<Category> categories) {
        LinearLayout categoryContainer = findViewById(R.id.category_container);
        SharedPreferences sharedPreferences = getSharedPreferences("category_images", MODE_PRIVATE);

        for (Category category : categories) {
            int imageResId = sharedPreferences.getInt("category_image_" + category.getName(), -1);
            if (imageResId != -1) {
                // 创建 ImageView 并设置图片
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(256, 256));
                imageView.setPadding(8, 0, 8, 0);

                // 设置图片
                imageView.setImageResource(imageResId);

                // 添加到 LinearLayout 中
                categoryContainer.addView(imageView);
            }
        }
    }


    private void saveCategoryImagePath(String categoryName, int resourceId) {
        SharedPreferences sharedPreferences = getSharedPreferences("category_images", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("category_image_" + categoryName, resourceId);
        editor.apply();
    }


    private void loadAllCategories() {
        // Load user categories
        Call<List<Category>> userCategoriesCall = apiService.getUserCategories(userId);
        userCategoriesCall.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCategories=response.body();
                    List<Category> filteredCategories = filterCategoriesByUserAndType(allCategories, userId);
                    loadCategoryImages(filteredCategories);
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to load user categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }





}