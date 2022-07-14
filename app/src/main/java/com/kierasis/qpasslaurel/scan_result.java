package com.kierasis.qpasslaurel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class scan_result extends AppCompatActivity {
    String code;
    TextView codeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        codeView = findViewById(R.id.codeView);
        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        codeView.setText(code);
    }
    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(scan_result.this,scan.class));
        Intent intent = new Intent(scan_result.this, scan.class);
        startActivity(intent);
        finish();
    }*/
}