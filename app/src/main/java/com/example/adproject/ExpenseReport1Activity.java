package com.example.adproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpenseReport1Activity extends AppCompatActivity {

    private static final String TAG = "ExpenseReportActivity";

    private BarChart summaryPieChart;
    private RecyclerView recentExpensesRecyclerView;
    private ApiService apiService;
    private Integer userId;
    private TextView totalExpensesAmount;
    private ImageButton backButton;
    private List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report1);

        summaryPieChart = findViewById(R.id.bar_chart);
        recentExpensesRecyclerView = findViewById(R.id.recent_expenses_recyclerview);

        totalExpensesAmount = findViewById(R.id.total_expenses_amount);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpenseReport1Activity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        apiService = ApiClient.getApiService();

        // 获取用户ID
        SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
        userId = pref.getInt("UserId", -1);

        fetchTotalSpendingByCategory();
        fetchRecentExpenses();
    }

    private void loadCategories() {
        // Load user categories and their budgets
        Call<List<Category>> userCategoriesCall = apiService.getUserCategories(userId);
        userCategoriesCall.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    fetchTotalSpendingByCategory();  // 获取支出并更新图表
                } else {
                    Toast.makeText(ExpenseReport1Activity.this, "Failed to load user categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(ExpenseReport1Activity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTotalSpendingByCategory() {
        Call<List<Map<String, Object>>> call = apiService.getTotalSpendingByCategoryForCurrentMonth(userId);
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> totalSpendingByCategory = response.body();
                    setupBarChart(totalSpendingByCategory);
                    double totalExpenses = calculateTotalExpenses(totalSpendingByCategory);
                    totalExpensesAmount.setText(String.format("$%.2f", totalExpenses));
                } else {
                    Toast.makeText(ExpenseReport1Activity.this, "Failed to load total spending by category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(ExpenseReport1Activity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double calculateTotalExpenses(List<Map<String, Object>> categorySpendings) {
        double total = 0;
        for (Map<String, Object> spending : categorySpendings) {
            Number spendingAmount = (Number) spending.get("totalSpending");
            if (spendingAmount != null) {
                total += spendingAmount.doubleValue();
            }
        }
        return total;
    }

    private void fetchRecentExpenses() {
        Call<List<Transaction>> call = apiService.getTransactionByUserId(userId);
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> transactions = response.body();
                    if (transactions.size() > 10) {
                        transactions = transactions.subList(0, 10);
                    }
                    setupRecyclerView(transactions);
                } else {
                    Toast.makeText(ExpenseReport1Activity.this, "Failed to load recent expenses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(ExpenseReport1Activity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView(List<Transaction> transactions) {
        ExpenseAdapter adapter = new ExpenseAdapter(transactions);
        recentExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentExpensesRecyclerView.setAdapter(adapter);
    }

    private void setupBarChart(List<Map<String, Object>> categorySpendings) {
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> categoryNames = new ArrayList<>();

        for (Map<String, Object> categorySpending : categorySpendings) {
            Number categoryIdNumber = (Number) categorySpending.get("categoryId");
            if (categoryIdNumber == null) continue;

            Integer categoryId = categoryIdNumber.intValue();
            Double totalSpending = (Double) categorySpending.get("totalSpending");

            Category matchingCategory = getCategoryById(categoryId);
            if (matchingCategory != null) {
                categoryNames.add(matchingCategory.getName());
                float budget = (float) matchingCategory.getBudget();
                barEntries.add(new BarEntry(barEntries.size(), new float[]{totalSpending.floatValue(), budget}));
            }
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Spending vs Budget");
        barDataSet.setColors(Color.RED, Color.GREEN);
        BarData data = new BarData(barDataSet);
        summaryPieChart.setData(data);
        summaryPieChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(categoryNames));
        summaryPieChart.invalidate(); // refresh
    }

    private Category getCategoryById(int categoryId) {
        for (Category category : categories) {
            if (category.getId() == categoryId) {
                return category;
            }
        }
        return null;
    }



//    private void fetchCategoryName(Integer categoryId, Double totalSpending, List<PieEntry> entries, int totalRequests, int[] completedRequests) {
//        Call<Category> call = apiService.getCategoryById(categoryId);
//        call.enqueue(new Callback<Category>() {
//            @Override
//            public void onResponse(Call<Category> call, Response<Category> response) {
//                if (response.isSuccessful()) {
//                    Category category = response.body();
//                    if (category != null) {
//                        //System.out.println(totalSpending.floatValue());
//                        //System.out.println(category.getName());
//                        entries.add(new PieEntry(totalSpending.floatValue(), category.getName()));
//                    }
//                } else {
//                    Toast.makeText(ExpenseReportActivity.this, "Failed to load category name", Toast.LENGTH_SHORT).show();
//                }
//                checkAndUpdatePieChart(entries, totalRequests, completedRequests);
//            }
//
//            @Override
//            public void onFailure(Call<Category> call, Throwable t) {
//                Toast.makeText(ExpenseReportActivity.this, "Network error", Toast.LENGTH_SHORT).show();
//                checkAndUpdatePieChart(entries, totalRequests, completedRequests);
//            }
//        });
//    }

//    private void checkAndUpdatePieChart(List<PieEntry> entries, int totalRequests, int[] completedRequests) {
//        completedRequests[0]++;
//        if (completedRequests[0] == totalRequests) {
//            updatePieChart(entries);
//        }
//    }
//
//    private void updatePieChart(List<PieEntry> entries) {
//        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
//        dataSet.setColors(new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
//        PieData data = new PieData(dataSet);
//
//        summaryPieChart.setData(data);
//        summaryPieChart.invalidate(); // refresh
//    }
}