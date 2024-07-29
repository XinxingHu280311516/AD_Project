package com.example.adproject;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;

public class ExpenseReportActivity extends AppCompatActivity {

    private PieChart summaryPieChart;
    private RecyclerView recentExpensesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        summaryPieChart = findViewById(R.id.summary_pie_chart);
        setupPieChart();

        recentExpensesRecyclerView = findViewById(R.id.recent_expenses_recyclerview);
        setupRecyclerView();
    }

    private void setupPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Groceries"));
        entries.add(new PieEntry(30f, "Rent"));
        entries.add(new PieEntry(20f, "Utilities"));
        entries.add(new PieEntry(10f, "Dining Out"));

        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
        dataSet.setColors(new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
        PieData data = new PieData(dataSet);

        summaryPieChart.setData(data);
        summaryPieChart.invalidate(); // refresh
    }

    private void setupRecyclerView() {
        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense("12 Oct 2023", "Groceries", "Walmart", 45.00));
        expenses.add(new Expense("10 Oct 2023", "Dining Out", "Italian Bistro", 120.00));
        expenses.add(new Expense("08 Oct 2023", "Electronics", "Best Buy", 300.00));

        ExpenseAdapter adapter = new ExpenseAdapter(expenses);
        recentExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentExpensesRecyclerView.setAdapter(adapter);
    }
}