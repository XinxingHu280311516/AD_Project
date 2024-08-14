package com.example.adproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.mlkit.vision.text.TextRecognition;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextRecognitionActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView imageView;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        Button captureButton = findViewById(R.id.btn_capture);
        Button selectButton = findViewById(R.id.btn_select);
        imageView = findViewById(R.id.imageView);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No camera available", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
                recognizeTextFromImage(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri imageUri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(imageBitmap);
                    recognizeTextFromImage(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void recognizeTextFromImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String recognizedText = visionText.getText();
                    String amount = extractAmount(recognizedText);
                    String date = extractDate(recognizedText);

                    System.out.println(recognizedText);

                    System.out.println(amount);
                    System.out.println(date);

                    if (amount != null || date != null) {
                        Intent intent = new Intent(TextRecognitionActivity.this, EnterExpenseActivity.class);
                        intent.putExtra("amount", amount);
                        intent.putExtra("date", date);
                        startActivity(intent);
                    } else {
                        Toast.makeText(TextRecognitionActivity.this, "No valid amount or date found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TextRecognition", "Text recognition failed", e);
                    Toast.makeText(TextRecognitionActivity.this, "Failed to recognize text", Toast.LENGTH_SHORT).show();
                });
    }


    private String extractAmount(String text) {
        // 匹配以 $ 开头或以 SGD 开头的金额（包括可能的千位分隔符和小数）
        Pattern pattern = Pattern.compile("(\\$|SGD|-)\\s?\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?");
        Matcher matcher = pattern.matcher(text);
        String lastMatch = null;

        // 找到最后一个匹配的金额
        while (matcher.find()) {
            lastMatch = matcher.group();
        }

        if (lastMatch != null) {
            // 使用正则表达式只保留数字和小数点
            lastMatch = lastMatch.replaceAll("[^\\d.]", "");
        }

        return lastMatch;
    }



    private String extractDate(String text) {
        // 匹配日期部分，支持 d/M/yyyy, dd/MM/yyyy, yyyy/M/d, yyyy/MM/dd, d MMM yyyy, yyyy MMM d 等格式
        Pattern datePattern = Pattern.compile(
                "\\d{1,2}[-/]\\d{1,2}[-/]\\d{4}|" +    // d/M/yyyy or dd/MM/yyyy
                        "\\d{4}[-/]\\d{1,2}[-/]\\d{1,2}|" +    // yyyy/M/d or yyyy/MM/dd
                        "\\d{1,2}\\s+\\w{3,9}\\s+\\d{4}|" +    // d MMM yyyy (e.g., 14 Aug 2024)
                        "\\d{4}\\s+\\w{3,9}\\s+\\d{1,2}"       // yyyy MMM d (e.g., 2024 Aug 14)
        );
        Matcher dateMatcher = datePattern.matcher(text);

        if (dateMatcher.find()) {
            String dateStr = dateMatcher.group();
            return formatDateString(dateStr);
        }

        return null;
    }

    private String formatDateString(String dateStr) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date;

        // 使用 DateTimeFormatterBuilder 来构建灵活的解析器，支持多种日期格式
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/M/d"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-d"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .appendOptional(DateTimeFormatter.ofPattern("d/M/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("d-M-yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("d MMM yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy MMM d"))
                .toFormatter();

        // 解析日期
        try {
            date = LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Unexpected date format: " + dateStr, e);
        }

        // 将 LocalDate 对象格式化为所需的 dd-MM-yyyy 格式
        return date.format(outputFormatter);
    }






}
