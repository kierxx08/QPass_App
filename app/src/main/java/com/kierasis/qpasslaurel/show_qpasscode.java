package com.kierasis.qpasslaurel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class show_qpasscode extends AppCompatActivity {
    ImageView qrcode;
    QRGEncoder qrgEncoder;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qpasscode);

        final SharedPreferences user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        String user_id = user_info.getString("key2", "");
        qrcode = findViewById(R.id.QRCode);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_showpass);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(user_id.length()>0){
            qrgEncoder = new QRGEncoder(user_id,null, QRGContents.Type.TEXT,500);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                qrcode.setImageBitmap(bitmap);
            }
            catch (WriterException e){
                Log.v("GenerateQrCode",e.toString());
            }
        }else{
            Toast.makeText(show_qpasscode.this,"Error",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}