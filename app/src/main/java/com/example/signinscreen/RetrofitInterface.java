package com.example.signinscreen;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @GET("historialPnl/{id}")
    Call<ArrayList<HistoricalPnl>> getHistoricalPnl(@Header("Authorization") String token,
                                                    @Path("id") int userId);

    @PUT("/auth")
    Call<User> executeLogin(@Body HashMap<String, String> map);

    @POST("/auth/password")
    Call<Void> executeForgotPassword(@Body HashMap<String, String> map);

    @DELETE("/auth")
    Call<Void> executeLogout(@Header("Authorization") String token);
}
