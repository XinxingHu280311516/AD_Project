package com.example.adproject;

import android.os.Bundle;
import android.graphics.Color;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private ProgressBar budgetProgressBar;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        budgetProgressBar = findViewById(R.id.budget_progress_bar);
        budgetProgressBar.setProgress(67); // 设置进度

        pieChart = findViewById(R.id.pie_chart);
        setupPieChart();
    }

    private void setupPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Groceries"));
        entries.add(new PieEntry(30f, "Rent"));
        entries.add(new PieEntry(20f, "Utilities"));
        entries.add(new PieEntry(10f, "Transportation"));

        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
        dataSet.setColors(new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW});
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate(); // refresh
    }
}
