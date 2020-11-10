package com.example.signinscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);


        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        Button loginBtn = findViewById(R.id.login_button);
        TextView loginErrorMsg = findViewById(R.id.login_error_message);
        EditText emailEdit = findViewById(R.id.login_email_edit);
        EditText passwordEdit = findViewById(R.id.login_password_edit);

        loginErrorMsg.setText("");

        HashMap<String, String> map = new HashMap<>();
        map.put("email", emailEdit.getText().toString());
        map.put("password", passwordEdit.getText().toString());

        Call<User> call = retrofitInterface.executeLogin(map);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    loginErrorMsg.setText("Wrong Credentials");
                    return;
                }

                // TODO: set redirection instead of modifying login button text:
                User userResponse = response.body();
                String content = "User: " + userResponse.getName() + "logged in successfully";
                loginBtn.setText(content);
                Toast.makeText(getApplicationContext(), "Redirecting...",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, NextActivity.class);
                startActivity(i);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loginErrorMsg.setText(t.getMessage());
            }
        });
    }
}