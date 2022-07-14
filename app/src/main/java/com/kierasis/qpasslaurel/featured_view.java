package com.kierasis.qpasslaurel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.leo.simplearcloader.SimpleArcLoader;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class featured_view extends AppCompatActivity {
    String caseid;
    TextView tvtitle,tvdate,tvfulldesc;
    ImageView photo;

    SimpleArcLoader loader_fv;
    ScrollView sv_featured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_view);

        tvtitle = findViewById(R.id.tvtitle);
        tvdate = findViewById(R.id.tvdate);
        tvfulldesc = findViewById(R.id.tvfulldesc);
        photo = findViewById(R.id.photo);

        Intent intent = getIntent();
        caseid = intent.getStringExtra("code");
        caseView(caseid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_fv);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loader_fv = findViewById(R.id.loader_featured);
        sv_featured = findViewById(R.id.sv_featured);
        loader_fv.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void caseView(final String caseid){

        String uRl = "https://bastaleakserver000.000webhostapp.com/QPass_App/update_view.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Success")){

                    String[] arrayString = response.split(";");
                    String title = arrayString[1];
                    String desc = arrayString[2];
                    desc = desc.replace("<tab>","     ");
                    desc = desc.replace("<newline>","\n\n     ");
                    String date = arrayString[3];
                    String img = arrayString[4];

                    tvtitle.setText(title);
                    tvfulldesc.setText(desc);
                    tvdate.setText(date);

                    /*
                    Glide.with(featured_view.this)
                            .load(img)
                            .centerCrop()
                            .placeholder(R.drawable.covid_update_banner)
                            .into(photo);*/

                    Picasso.get()
                            .load(img)
                            .placeholder(R.drawable.no_image_featured)
                            .into(photo);

                    loader_fv.stop();
                    loader_fv.setVisibility(View.GONE);
                    sv_featured.setVisibility(View.VISIBLE);


                }else {

                    Toast.makeText(featured_view.this, response, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                photo.setBackgroundResource(R.drawable.no_net_featured);

                if((error.toString().contains("html"))||(error.toString().contains("<!DOCTYPE html>"))){
                    Toast.makeText(featured_view.this, "App Expired. Please Update.", Toast.LENGTH_SHORT).show();
                }else if(error.toString().contains("end of stream")){
                    Toast.makeText(featured_view.this, "Runtime Timeout", Toast.LENGTH_SHORT).show();
                }else if (error.toString().contains("Unable to resolve host")) {
                    Toast.makeText(featured_view.this, "Server Error. Please Try Again.", Toast.LENGTH_SHORT).show();
                    //}else if(error.toString().contains("NoConnectionError")) {
                    //    Toast.makeText(MainActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(featured_view.this, error.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("caseNo", caseid);
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(featured_view.this).addToRequestQueue(request);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}