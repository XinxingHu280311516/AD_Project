package com.example.adproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}