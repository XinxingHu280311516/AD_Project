package com.example.adproject;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpenseReportActivity extends AppCompatActivity {

    private static final String TAG = "ExpenseReportActivity";

    private PieChart summaryPieChart;
    private RecyclerView recentExpensesRecyclerView;
    private ApiService apiService;
    private Integer userId;
    private TextView totalExpensesAmount;
    private List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        summaryPieChart = findViewById(R.id.summary_pie_chart);
        recentExpensesRecyclerView = findViewById(R.id.recent_expenses_recyclerview);

        totalExpensesAmount = findViewById(R.id.total_expenses_amount);

        apiService = ApiClient.getApiService();

        // 获取用户ID
        SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
        userId = pref.getInt("UserId", -1);

        fetchTotalSpendingByCategory();
        fetchRecentExpenses();
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
                    Toast.makeText(ExpenseReportActivity.this, "Failed to load recent expenses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(ExpenseReportActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView(List<Transaction> transactions) {
        ExpenseAdapter adapter = new ExpenseAdapter(transactions);
        recentExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentExpensesRecyclerView.setAdapter(adapter);
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
                    totalExpensesAmount.setText(String.format("$%.2f", totalExpenses));
                } else {
                    Toast.makeText(ExpenseReportActivity.this, "Failed to load total spending by category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(ExpenseReportActivity.this, "Network error", Toast.LENGTH_SHORT).show();
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
                Log.e(TAG, "Category ID is null for entry: " + categorySpending);
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
                    Toast.makeText(ExpenseReportActivity.this, "Failed to load category name", Toast.LENGTH_SHORT).show();
                }
                checkAndUpdatePieChart(entries, totalRequests, completedRequests);
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(ExpenseReportActivity.this, "Network error", Toast.LENGTH_SHORT).show();
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

        summaryPieChart.setData(data);
        summaryPieChart.invalidate(); // refresh
    }
}
