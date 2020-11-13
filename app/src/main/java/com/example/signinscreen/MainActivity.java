package com.example.signinscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        setSigninScreen();
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
                    System.out.println( "Code: " + response.code() + "Error: " + response.message());
                    loginErrorMsg.setText("Wrong Credentials");
                    return;
                }

                // After successful login: moving to next layout + setting up logout listener
                token = response.body().getUserDetails().getToken();
                List<User> users = response.body().getUsers();
                setNextView(users, token);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println( "t.getMessage(): " + t.getMessage());

                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                loginErrorMsg.setText(t.getMessage());
            }
        });
    }

    private void setNextView(List<User> users, String token) {
        setContentView(R.layout.activity_next);
        TextView usersListText = findViewById(R.id.next_activity_text);

        for(User user : users) {
            int id =  user.getId();

            String content = "";
            content += "ID: " + id + "\n";
            content += "Username: " + user.getUsername() + "\n\n";
            usersListText.append(content);

            usersListText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleGetHistoricalPnl(token, id);
                }
            });
        }

        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout(token);
            }
        });
    }

    private void handleGetHistoricalPnl(String token, int id) {
        String bearerAuth = "bearerAuth: " + token;

        Call<ArrayList<HistoricalPnl>> call = retrofitInterface.getHistoricalPnl(bearerAuth, 3);

        call.enqueue(new Callback<ArrayList<HistoricalPnl>>() {
            @Override
            public void onResponse(Call<ArrayList<HistoricalPnl>> call, Response<ArrayList<HistoricalPnl>> response) {
                if (!response.isSuccessful()) {
                    System.out.println( "Code: " + response.code() + "Error: " + response.message());
                    return;
                }

                ArrayList<HistoricalPnl> pnl = response.body();
                for (HistoricalPnl historicalPnl : pnl) {
                    int month = historicalPnl.getMonth();
                    int year = historicalPnl.getYear();
                    float salesPnl = historicalPnl.getPnl();

                    // TODO: present result after successful request
                    System.out.println(month + "/" + year + " // sales pnl - " + salesPnl);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<HistoricalPnl>> call, Throwable t) {
                System.out.println("t.getMessage():  "  + t.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
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

                // Moving to previous sing in layout after successful logout:
                setSigninScreen();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleForgotPassword() {
        setContentView(R.layout.forgot_password);

        findViewById(R.id.forgot_password_send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendForgotPasswordEmail();
            }
        });

        findViewById(R.id.forgot_password_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSigninScreen();
            }
        });
    }

    private void setSigninScreen() {
        setContentView(R.layout.activity_main);
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

    private void handleSendForgotPasswordEmail() {
        EditText username = findViewById(R.id.login_username_edit);
        String user = username.getText().toString();
        TextView resetErrorMsg = findViewById(R.id.forgot_password_error_message);
        Button sendBtn = findViewById(R.id.forgot_password_send_button);

        resetErrorMsg.setText("");

        HashMap<String, String> map = new HashMap<>();
        map.put("username", user);

        Call<Void> call = retrofitInterface.executeForgotPassword(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()) {
                    resetErrorMsg.setText("Wrong Username");
                    return;
                }
                sendBtn.setText("Sent!");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}