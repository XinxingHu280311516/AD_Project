package com.example.adproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SavingsTipsActivity extends AppCompatActivity {

    private TextView detailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_tips);

        detailsTextView = findViewById(R.id.details_text_view);

        // 从 Intent 中获取数据并显示
        String details = getIntent().getStringExtra("details");
        String processedText = details.replaceAll("#", "").replaceAll("\\*", "");
        detailsTextView.setText(processedText);
    }
}