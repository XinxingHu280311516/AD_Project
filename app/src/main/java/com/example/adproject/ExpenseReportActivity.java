package com.example.adproject;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

public class ExpenseReportActivity extends AppCompatActivity {

    private PieChart summaryPieChart;
    private RecyclerView recentExpensesRecyclerView;
    private ApiService apiService;
    private Integer userId;
    private List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        summaryPieChart = findViewById(R.id.summary_pie_chart);
        recentExpensesRecyclerView = findViewById(R.id.recent_expenses_recyclerview);

        apiService = ApiClient.getApiService();

        // 获取用户ID
        SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
        userId = pref.getInt("UserId", -1);

        fetchTotalSpendingByCategory();
    }

    private void fetchTotalSpendingByCategory() {
        Call<List<Object[]>> call = apiService.getTotalSpendingByCategoryForCurrentMonth(userId);
        call.enqueue(new Callback<List<Object[]>>() {
            @Override
            public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                if (response.isSuccessful()) {
                    List<Object[]> categorySpendings = response.body();
                    setupPieChart(categorySpendings);
                } else {
                    Toast.makeText(ExpenseReportActivity.this, "Failed to load category spendings", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Object[]>> call, Throwable t) {
                Toast.makeText(ExpenseReportActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPieChart(List<Object[]> categorySpendings) {
        List<PieEntry> entries = new ArrayList<>();
        for (Object[] categorySpending : categorySpendings) {
            Number categoryIdNumber = (Number) categorySpending[0];
            Integer categoryId = categoryIdNumber.intValue(); // 将 Number 转换为 Integer
            Double totalSpending = (Double) categorySpending[1];
            fetchCategoryName(categoryId, totalSpending, entries);
        }
    }

    private void fetchCategoryName(Integer categoryId, Double totalSpending, List<PieEntry> entries) {
        Call<Category> call = apiService.getCategoryById(categoryId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    Category category = response.body();
                    if (category != null) {
                        entries.add(new PieEntry(totalSpending.floatValue(), category.getName()));
                        updatePieChart(entries);
                    }
                } else {
                    Toast.makeText(ExpenseReportActivity.this, "Failed to load category name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(ExpenseReportActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePieChart(List<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
        dataSet.setColors(new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
        PieData data = new PieData(dataSet);

        summaryPieChart.setData(data);
        summaryPieChart.invalidate(); // refresh
    }
}
