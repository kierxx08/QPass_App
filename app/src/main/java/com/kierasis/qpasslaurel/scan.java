package com.kierasis.qpasslaurel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class scan extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannView;
    TextView resultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        scannView = findViewById(R.id.scan);
        codeScanner = new CodeScanner(this,scannView);
        resultData = findViewById(R.id.text);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,100);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.EFFECT_HEAVY_CLICK));
                        } else {
                            //deprecated in API 26
                            v.vibrate(100);
                        }
                        //resultData.setText(result.getText());

                        Intent intent = new Intent(scan.this, scan_result.class);
                        intent.putExtra("code", result.getText());
                        startActivity(intent);
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }


    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        startActivity(new Intent(scan.this,scan.class));
        finish();

    }

    private void requestForCamera() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        codeScanner.startPreview();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(scan.this,"Camera Permission is Required",Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}