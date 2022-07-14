package com.kierasis.qpasslaurel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class change_profile_pic extends AppCompatActivity {

    MaterialEditText fb_id;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_pic);

        fb_id = findViewById(R.id.fb_id);
        submit = findViewById(R.id.fb_submit);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cpp);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        final SharedPreferences user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        if(!Objects.equals(user_info.getString("user_fb_id", ""), "")){
            MaterialEditText editText = (MaterialEditText)findViewById(R.id.fb_id);
            final String fb_id = user_info.getString("user_fb_id", "");
            editText.setText(fb_id, TextView.BufferType.EDITABLE);
        }
        

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = user_info.edit();
                editor.putString("user_fb_id",fb_id.getText().toString());
                editor.apply();
                Toast.makeText(change_profile_pic.this,"Success",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}