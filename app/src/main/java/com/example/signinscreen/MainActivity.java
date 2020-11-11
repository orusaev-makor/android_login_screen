package com.example.signinscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
//    private String BASE_URL = "http://10.0.2.2:3000/";
    private String BASE_URL = "http://api-pnl.enigma-securities.io";
    private static String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        findViewById(R.id.login_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });
    }

    private void handleLogin() {
        TextView loginErrorMsg = findViewById(R.id.login_error_message);
        EditText username = findViewById(R.id.login_username_edit);
        EditText passwordEdit = findViewById(R.id.login_password_edit);

        loginErrorMsg.setText("");

        HashMap<String, String> map = new HashMap<>();
        map.put("username", username.getText().toString());
        map.put("password", passwordEdit.getText().toString());

        Call<User> call = retrofitInterface.executeLogin(map);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    loginErrorMsg.setText("Wrong Credentials");
                    return;
                }

                // Moving to next layout + setting up logout listener after successful login:
                token = response.body().getUserDetails().getToken();
                setContentView(R.layout.activity_next);

                findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleLogout(token);
                    }
                });
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                loginErrorMsg.setText(t.getMessage());
            }
        });
    }

    private void handleLogout(String token) {
        String bearerAuth = "bearerAuth: " + token;
        Call<Void> call = retrofitInterface.executeLogout(bearerAuth);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                // Moving to previous sing in layout + setting up login listener (again???) after successful logout:
                setContentView(R.layout.activity_main);
                findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleLogin();
                    }
                });
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleForgotPassword() {
        EditText username = findViewById(R.id.login_username_edit);
        String user = username.getText().toString();

        HashMap<String, String> map = new HashMap<>();
        map.put("username", user);

        Call<Void> call = retrofitInterface.executeForgotPassword(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()) {
                    // TODO: check if forgot password error should be presented in TextView id: "login_error_message" ?
                    Toast.makeText(MainActivity.this,
                            "Wrong username", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(MainActivity.this,
                        "Forgot password email sent to user: " + user, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}