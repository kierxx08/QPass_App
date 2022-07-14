package com.kierasis.qpasslaurel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class hidden_info extends AppCompatActivity {
    TextView tv_access_id, tv_device_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_info);
        tv_access_id = findViewById(R.id.hi_access_id);
        tv_device_id = findViewById(R.id.hi_device_id);

        SharedPreferences user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        SharedPreferences device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        String access_id = user_info.getString(getResources().getString(R.string.prefLoginKey), "");
        String device_id = device_info.getString("device_key", "");

        tv_access_id.setText(access_id);
        tv_device_id.setText(device_id);
    }
}