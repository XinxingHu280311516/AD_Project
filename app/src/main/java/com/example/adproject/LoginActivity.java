package com.example.adproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {




    private EditText emailEditText, passwordEditText;
    private Button signInButton;
    private ApiService apiService;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in_button);

        // Initialize Retrofit API service
        apiService = ApiClient.getApiService();



        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                } else {
                    login(email, password);
                }
            }
        });

        TextView signUpText = findViewById(R.id.sign_up_text);
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(String email, String password) {
        User user = new User();
        user.setUsername(email); // Assuming username is the same as email for this example
        user.setPassword(password);

        Call<User> call = apiService.login(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User loggedInUser = response.body();
                    userId = loggedInUser.getId();
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                } else {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("LoginActivity", "onFailure: ", t);
                Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        SharedPreferences pref = getSharedPreferences("isFirstLogin", MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("isFirst", false);
        SharedPreferences pref1 = getSharedPreferences("userId", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = pref1.edit();
        editor1.putInt("UserId",userId);
        editor1.apply();
        if(isFirst){
            Intent intent = new Intent(LoginActivity.this, ExpenseCategoryManagementActivity.class);
            startActivity(intent);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("isFirst");
            editor.apply();
            finish();
        }
        else {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
