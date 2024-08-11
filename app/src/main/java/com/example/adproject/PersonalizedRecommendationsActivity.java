package com.example.adproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Map;

public class PersonalizedRecommendationsActivity extends AppCompatActivity {

    private TextView spendingOverviewText;
    private TextView budgetAdjustmentText;
    private TextView savingsTipsText;

    private ApiService apiService;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalized_recommendations);

        spendingOverviewText = findViewById(R.id.spending_overview_text);
        budgetAdjustmentText = findViewById(R.id.budget_adjustment_text);
        savingsTipsText = findViewById(R.id.savings_tips_text);

        apiService = ApiClient.getApiService();

        // 从 SharedPreferences 或 Intent 中获取 userId
        SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
        userId = pref.getInt("UserId", -1);

        Call<List<Map<String, String>>> call = apiService.getBudgetAdjustmentTips(userId);

        call.enqueue(new Callback<List<Map<String, String>>>() {
            @Override
            public void onResponse(Call<List<Map<String, String>>> call, Response<List<Map<String, String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, String>> tips = response.body();  // 直接获取响应内容
                    updateUI(tips); // 更新 UI 显示
                } else {
                    spendingOverviewText.setText("Failed to load data");
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, String>>> call, Throwable t) {
                System.out.println("Request failed: " + t.getMessage());
                t.printStackTrace(); // 打印完整的堆栈跟踪信息
                spendingOverviewText.setText("Network error: " + t.getMessage());
            }
        });
    }

    private void updateUI(List<Map<String, String>> tips) {
        for (Map<String, String> tip : tips) {
            String name = tip.get("name");
            String value = tip.get("value");

            if ("Spending Overview".equals(name)) {
                spendingOverviewText.setText(value);
            } else if ("Budget Adjustment Suggestions".equals(name)) {
                budgetAdjustmentText.setText(value);
            } else if ("Savings Tips".equals(name)) {
                savingsTipsText.setText(value);
            }
        }
    }
}
