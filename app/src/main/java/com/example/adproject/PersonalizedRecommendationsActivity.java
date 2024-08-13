package com.example.adproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Map;

public class PersonalizedRecommendationsActivity extends AppCompatActivity {

    private TextView spendingOverviewText;
    //private TextView budgetAdjustmentText;
    //private TextView savingsTipsText;

    private Button budgetAdjustmentButton;
    private Button savingsTipsButton;

    private RelativeLayout loadingLayout;

    private ApiService apiService;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalized_recommendations);

        spendingOverviewText = findViewById(R.id.spending_overview_text);
        //budgetAdjustmentText = findViewById(R.id.budget_adjustment_text);
        //savingsTipsText = findViewById(R.id.savings_tips_text);

        budgetAdjustmentButton = findViewById(R.id.budget_adjustment_button);
        savingsTipsButton = findViewById(R.id.savings_tips_button);

        loadingLayout = findViewById(R.id.loading_layout);

        ImageButton backButton = findViewById(R.id.back_button6);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalizedRecommendationsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        showLoading();

        apiService = ApiClient.getApiService();

        // 从 SharedPreferences 或 Intent 中获取 userId
        SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
        userId = pref.getInt("UserId", -1);

        Call<List<Map<String, String>>> call = apiService.getBudgetAdjustmentTips(userId);

        call.enqueue(new Callback<List<Map<String, String>>>() {
            @Override
            public void onResponse(Call<List<Map<String, String>>> call, Response<List<Map<String, String>>> response) {
                hideLoading();
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

        budgetAdjustmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalizedRecommendationsActivity.this, BudgetAdjustmentActivity.class);
                intent.putExtra("details", budgetAdjustmentButton.getTag().toString());
                startActivity(intent);
            }
        });

        savingsTipsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalizedRecommendationsActivity.this, SavingsTipsActivity.class);
                intent.putExtra("details", savingsTipsButton.getTag().toString());
                startActivity(intent);
            }
        });
    }

    private void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
    }


    private void updateUI(List<Map<String, String>> tips) {
        for (Map<String, String> tip : tips) {
            String name = tip.get("name");
            String value = tip.get("value");

            if ("Spending Overview".equals(name)) {
                String processedText = value.replaceAll("#", "").replaceAll("\\*", "");
                spendingOverviewText.setText(processedText);
            } else if ("Budget Adjustment Suggestions".equals(name)) {
                budgetAdjustmentButton.setTag(value);  // 将内容存储在按钮的Tag中
            } else if ("Savings Tips".equals(name)) {
                savingsTipsButton.setTag(value);  // 将内容存储在按钮的Tag中
            }
        }
    }
}
