package com.example.adproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private ProgressBar budgetProgressBar;
    private PieChart pieChart;
    private ApiService apiService;
    private Integer userId;
    private TextView totalExpensesAmount,budgetProgressPercentage;
    private double totalBudget;
    private TextView topExpensesTitle1, topExpensesTitle2,  topExpensesTitle3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        apiService = ApiClient.getApiService();

        // 获取用户ID
        SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
        userId = pref.getInt("UserId", -1);

        totalExpensesAmount = findViewById(R.id.total_expenses_amount);

        budgetProgressBar = findViewById(R.id.budget_progress_bar);
        //budgetProgressBar.setProgress(67); // 设置进度

        budgetProgressPercentage = findViewById(R.id.budget_progress_percentage);

        pieChart = findViewById(R.id.pie_chart);

        ImageButton backButton = findViewById(R.id.back_button2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        //setupPieChart();
        topExpensesTitle1 = findViewById(R.id.top_expenses_title_1);
        topExpensesTitle2 = findViewById(R.id.top_expenses_title_2);
        topExpensesTitle3 = findViewById(R.id.top_expenses_title_3);

        //fetchTotalSpendingByCategory();
        fetchDashboardData();
    }

    private void fetchDashboardData() {
        totalBudget = 0; // 每次调用时将 totalBudget 重置为 0
        fetchTotalBudget(new BudgetFetchCallback() {
            @Override
            public void onBudgetFetched() {
                fetchTotalSpendingByCategory();
            }
        });
    }

    private interface BudgetFetchCallback {
        void onBudgetFetched();
    }

    private void fetchTotalBudget(BudgetFetchCallback callback) {
        Call<List<Category>> call = apiService.getUserCategories(userId);
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    for (Category category : categories) {
                        totalBudget += category.getBudget();
                    }
                    if (callback != null) {
                        callback.onBudgetFetched();
                    }
                    //updateBudgetProgress(totalBudget);
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load total budget", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBudgetProgress(double totalExpenses) {
        int progress = (int) ((totalExpenses / totalBudget) * 100);
        System.out.println("UP:"+totalBudget);
//        System.out.println("UP1:"+progress);
        budgetProgressBar.setProgress(progress);
        budgetProgressPercentage.setText(progress + "%");
        totalExpensesAmount.setText(String.format("$%.2f", totalExpenses));

    }


    private double calculateTotalExpenses(List<Map<String, Object>> categorySpendings) {
        double total = 0;
        for (Map<String, Object> spending : categorySpendings) {
            Number spendingAmount = (Number) spending.get("totalSpending");
            if (spendingAmount != null) {
                total += spendingAmount.doubleValue();
            }
        }
        //System.out.println(total);
        return total;
    }

    private void fetchTotalSpendingByCategory() {
        Call<List<Map<String, Object>>> call = apiService.getTotalSpendingByCategoryForCurrentMonth(userId);
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> totalSpendingByCategory = response.body();
                    setupPieChart(totalSpendingByCategory);

                    double totalExpenses = calculateTotalExpenses(totalSpendingByCategory);
                    System.out.println("totalBud:"+totalBudget);
                    updateBudgetProgress(totalExpenses);
                    totalExpensesAmount.setText(String.format("$%.2f", totalExpenses));
                    System.out.println(String.format("$%.2f", totalExpenses));
                    updateTopExpenses(totalSpendingByCategory);
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load total spending by category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPieChart(List<Map<String, Object>> categorySpendings) {
        List<PieEntry> entries = new ArrayList<>();
        int totalRequests = categorySpendings.size();
        int[] completedRequests = {0};

        for (Map<String, Object> categorySpending : categorySpendings) {
            Number categoryIdNumber = (Number) categorySpending.get("categoryId");
            if (categoryIdNumber == null) {
                //Log.e(TAG, "Category ID is null for entry: " + categorySpending);
                continue;
            }
            Integer categoryId = categoryIdNumber.intValue(); // 将 Number 转换为 Integer
            Double totalSpending = (Double) categorySpending.get("totalSpending");
            fetchCategoryName(categoryId, totalSpending, entries, totalRequests, completedRequests);
        }
    }

    private void fetchCategoryName(Integer categoryId, Double totalSpending, List<PieEntry> entries, int totalRequests, int[] completedRequests) {
        Call<Category> call = apiService.getCategoryById(categoryId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    Category category = response.body();
                    if (category != null) {
                        System.out.println(totalSpending.floatValue());
                        System.out.println(category.getName());
                        entries.add(new PieEntry(totalSpending.floatValue(), category.getName()));
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load category name", Toast.LENGTH_SHORT).show();
                }
                checkAndUpdatePieChart(entries, totalRequests, completedRequests);
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                checkAndUpdatePieChart(entries, totalRequests, completedRequests);
            }
        });
    }

    private void checkAndUpdatePieChart(List<PieEntry> entries, int totalRequests, int[] completedRequests) {
        completedRequests[0]++;
        if (completedRequests[0] == totalRequests) {
            updatePieChart(entries);
        }
    }

    private void updatePieChart(List<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
        dataSet.setColors(new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate(); // refresh
    }

    private void updateTopExpenses(List<Map<String, Object>> categorySpendings) {
        Collections.sort(categorySpendings, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Double spending1 = (Double) o1.get("totalSpending");
                Double spending2 = (Double) o2.get("totalSpending");
                return spending2.compareTo(spending1);
            }
        });

        if (categorySpendings.size() > 0) {
            Map<String, Object> top1 = categorySpendings.get(0);
            fetchCategoryNameForTopExpense(top1, topExpensesTitle1);
        }
        if (categorySpendings.size() > 1) {
            Map<String, Object> top2 = categorySpendings.get(1);
            fetchCategoryNameForTopExpense(top2, topExpensesTitle2);
        }
        if (categorySpendings.size() > 2) {
            Map<String, Object> top3 = categorySpendings.get(2);
            fetchCategoryNameForTopExpense(top3, topExpensesTitle3);
        }
    }

    private void fetchCategoryNameForTopExpense(Map<String, Object> categorySpending, TextView textView) {
        Integer categoryId = ((Number) categorySpending.get("categoryId")).intValue();
        Double totalSpending = (Double) categorySpending.get("totalSpending");
        Call<Category> call = apiService.getCategoryById(categoryId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    Category category = response.body();
                    if (category != null) {
                        textView.setText(category.getName() + " - $" + String.format("%.2f", totalSpending));
                    }
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Failed to load category name", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
