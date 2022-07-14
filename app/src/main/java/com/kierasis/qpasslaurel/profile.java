package com.kierasis.qpasslaurel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {
    CircleImageView profile_pic;
    int click_count = 1;
    TextView tvfullname, tvusername, tvaddress, tvphone, tvbday, tvgender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final SharedPreferences user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        String fullname = user_info.getString("user_fname", "") + " " + user_info.getString("user_lname", "");
        String username = user_info.getString("user_name", "");
        String address = user_info.getString("user_add2", "") + ", " + user_info.getString("user_brgy", "") + "" ;
        String phone = "+"+user_info.getString("user_mobile", "");
        String bday = user_info.getString("user_bday", "");
        String gender = user_info.getString("user_gender", "");

        tvfullname = findViewById(R.id.tv_fullname);
        tvusername = findViewById(R.id.tv_username);
        tvaddress = findViewById(R.id.tv_address);
        tvphone = findViewById(R.id.tv_phone);
        tvbday = findViewById(R.id.tv_bday);
        tvgender = findViewById(R.id.tv_gender);

        tvfullname.setText(fullname);
        tvusername.setText(username);
        tvaddress.setText(address);
        tvphone.setText(phone);
        tvbday.setText(bday);
        tvgender.setText(gender);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_prfl);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        Drawable profile_dw;

        if(gender.equals("Female")){
            profile_dw = getDrawable(R.drawable.profile_female);
        }else{
            profile_dw = getDrawable(R.drawable.profile_male);
        }

        profile_pic = findViewById(R.id.profile_image);

        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {

            profile_pic.setImageDrawable(profile_dw);

        }else {
            if(!Objects.equals(user_info.getString("user_fb_id", ""), "")) {
                final String fb_id = user_info.getString("user_fb_id", "");
                String urlLink = "https://graph.facebook.com/" + fb_id + "/picture?height=500&width=500";
                Glide.with(profile.this)
                        .load(urlLink)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .override(200, 200)
                        .dontAnimate()
                        .placeholder(profile_dw)
                        .into(profile_pic);
            }else {
                profile_pic.setImageDrawable(profile_dw);
            }
        }

        CircleImageView img = (CircleImageView) findViewById(R.id.profile_image);
        img.setOnClickListener( new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(click_count==10) {
                    startActivity(new Intent(profile.this,change_profile_pic.class));
                    click_count = 0;
                }
                click_count = click_count + 1;

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}