package com.example.signinscreen;

import com.google.gson.annotations.SerializedName;

public class HistoricalPnl {

    @SerializedName("pl_month")
    private int month;

    @SerializedName("pl_year")
    private int year;

    @SerializedName("sales_pl")
    private float pnl;

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public float getPnl() {
        return pnl;
    }
}
