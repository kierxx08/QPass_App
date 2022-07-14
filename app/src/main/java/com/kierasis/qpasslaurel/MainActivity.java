package com.kierasis.qpasslaurel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.provider.MediaStore.Images.Media.getBitmap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,featured_adapter.OnNoteListener {
    private long backPressedTime;
    private Toast backToast;
    private int longClickDuration = 3000;
    private boolean isLongPress = false;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;

    SwipeRefreshLayout refresh;

    RecyclerView recyclerView;
    List<featured_helper_class> cases;
    private  static String JSON_URL = "https://bastaleakserver000.000webhostapp.com/QPass_App/latest_update_json.php";
    featured_adapter adapter;

    SimpleArcLoader loader1;
    ImageView no_net;

    CardView cardView2, cardView3, cardView4, cardView5;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        final String gender = user_info.getString("user_gender", "");
        final String fname = user_info.getString("user_fname", "");
        final String lname = user_info.getString("user_lname", "");
        final String brgy = user_info.getString("user_brgy", "");
        final String add2 = user_info.getString("user_add2", "");
        final String user_id = user_info.getString("key2", "");
        sharedPreferences = getSharedPreferences("user-info", Context.MODE_PRIVATE);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.uname);
        nav_user.setText(fname + " " + lname);
        TextView nav_address = (TextView)hView.findViewById(R.id.uaddress);
        nav_address.setText(add2 + ", " + brgy + "");

        final LinearLayout header_bg = (LinearLayout) hView.findViewById(R.id.header_bg);
        //String urll = "https://www.hartmann.info/en-corp/-/media/corporate-news/stage/coronavirus_header_header-(003).jpg?la=en-TT&h=725&w=1440&mw=1440&hash=FA6C93681FAF12A50F462D1C1DB39137";
        Glide.with(this).load(getDrawable(R.drawable.cover_photo)).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    header_bg.setBackground(resource);
                }
            }
        });

        CircleImageView icon_profile = (CircleImageView)hView.findViewById(R.id.icon_profile);

        Drawable profile_dw;

        if(gender.equals("Female")){
            profile_dw = getDrawable(R.drawable.profile_female);
        }else{
            profile_dw = getDrawable(R.drawable.profile_male);
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {

            icon_profile.setImageDrawable(profile_dw);

        }else {
            if(!Objects.equals(user_info.getString("user_fb_id", ""), "")) {
                final String fb_id = user_info.getString("user_fb_id", "");
                String urlLink = "https://graph.facebook.com/" + fb_id + "/picture?height=500&width=500";
                Glide.with(MainActivity.this)
                        .load(urlLink)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .override(200, 200)
                        .dontAnimate()
                        .placeholder(profile_dw)
                        .into(icon_profile);
            }else {
                icon_profile.setImageDrawable(profile_dw);
            }
        }

        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);


        recyclerView = findViewById(R.id.featured_recycler);
        cases = new ArrayList<>();
        extractData();
        loader1 = findViewById(R.id.loader_main);
        no_net = findViewById(R.id.img_no_net);
        loader1.start();
        refresh = findViewById(R.id.customSwipeRefresh);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                refresh.setEnabled(false);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        refresh.setEnabled(true);
                        break;
                }
                return false;
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.setRefreshing(false);
                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }, 1000);


            }
        });

        cardView2 = findViewById(R.id.cardView2);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,tracker.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        cardView3 = findViewById(R.id.cardView3);
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,show_qpasscode.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        cardView4 = findViewById(R.id.cardView4);
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Coming Soon",Toast.LENGTH_SHORT).show();

            }
        });
        cardView5 = findViewById(R.id.cardView5);
        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,scan.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

    }


    private void extractData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject caseObject = response.getJSONObject(i);
                        featured_helper_class news = new featured_helper_class();
                        news.setTitle(caseObject.getString("title").toString());
                        news.setArtist(caseObject.getString("sm_desc").toString());
                        news.setCoverImage(caseObject.getString("img").toString());
                        news.setSongURL(caseObject.getString("date").toString());
                        cases.add(news);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*
                    simpleArcLoader3.stop();
                    simpleArcLoader3.setVisibility(View.GONE);
                    caselist.setVisibility(View.VISIBLE);*/

                }

                loader1.stop();
                loader1.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
                adapter = new featured_adapter(MainActivity.this,cases,MainActivity.this);
                recyclerView.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader1.stop();
                loader1.setVisibility(View.GONE);
                no_net.setVisibility(View.VISIBLE);
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        });

        queue.add(jsonArrayRequest);
    }

    @Override
    public void onNoteClick(int position) {
        Log.d("tag","onNoteClick: clicked.");

        Intent intent = new Intent(MainActivity.this, featured_view.class);
        intent.putExtra("code",String.valueOf(position));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {

            if (backPressedTime + 2000 >System.currentTimeMillis()){
                backToast.cancel();
                super.onBackPressed();
                return;
            }else{
                backToast = Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_scan:
                startActivity(new Intent(MainActivity.this,scan.class));
                break;
            case R.id.nav_corona_cases:
                startActivity(new Intent(MainActivity.this,case_list.class));
                break;
            case R.id.nav_corona_tracker:
                startActivity(new Intent(MainActivity.this,tracker.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this,profile.class));
                break;

            case R.id.nav_logout:
                String login_status = sharedPreferences.getString(getResources().getString(R.string.prefLoginState),"");
                if(login_status.equals("loggedin")) {
                    String user_key = sharedPreferences.getString(getResources().getString(R.string.prefLoginKey), "");
                    logout(user_key);
                }
                break;

            case R.id.nav_share:
                Toast.makeText(this,"Share",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about:
                //Toast.makeText(this,"About",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,about.class));
                break;

        }

        return false;
    }


    private void logout(final String key){
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Logging out");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://bastaleakserver000.000webhostapp.com/QPass_App/logout.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Logout Success")){
                    progressDialog.dismiss();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getResources().getString(R.string.prefLoginState),"loggedout");
                    editor.putString(getResources().getString(R.string.prefLoginKey),"");
                    editor.apply();
                    startActivity(new Intent(MainActivity.this,login.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    Toast.makeText(MainActivity.this,"Logout Success",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if((error.toString().contains("html"))||(error.toString().contains("<!DOCTYPE html>"))){
                    Toast.makeText(MainActivity.this, "App Expired. Please Update.", Toast.LENGTH_SHORT).show();
                }else if(error.toString().contains("end of stream")){
                    Toast.makeText(MainActivity.this, "Runtime Timeout", Toast.LENGTH_SHORT).show();
                }else if (error.toString().contains("Unable to resolve host")) {
                    Toast.makeText(MainActivity.this, "Server Error. Please Try Again.", Toast.LENGTH_SHORT).show();
                    //}else if(error.toString().contains("NoConnectionError")) {
                    //    Toast.makeText(MainActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("key", key);
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);

    }

}