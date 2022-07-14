package com.kierasis.qpasslaurel;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class splash extends AppCompatActivity {
    public static String PACKAGE_NAME;

    public SharedPreferences device_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        PACKAGE_NAME = getApplicationContext().getPackageName();
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


        int error = 0;
        String error_desc = "";
        if (versionCode != 1) {
            error = 1;
            error_desc = "System Error. Version Code Tampered.";
        } else if (versionName != "1.0") {
            error = 1;
            error_desc = "System Error. Version Name Tampered.";
        } else if (!PACKAGE_NAME.equals("com.kierasis.qpasslaurel")) {
            error = 1;
            error_desc = "System Error. Package Name Tampered.";
        }

        if(error == 1){


            // Build an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);

            // Set a title for alert dialog
            builder.setTitle("App Modified");

            // Ask the final question
            builder.setMessage(error_desc);

            // Set click listener for alert dialog buttons
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case DialogInterface.BUTTON_NEUTRAL:
                            // Neutral/Cancel button clicked
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            startActivity(intent);
                            break;
                    }
                }
            };

            // Set the alert dialog cancel/neutral button click listener
            builder.setNeutralButton("Exit", dialogClickListener);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();

        }else{
                if (Objects.equals(device_info.getString("device_key", ""), "")) {
                    check_app(versionName);
                }else if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                    if(!Objects.equals(device_info.getString("latest_version", ""), "")){
                        String latest_vrsn = device_info.getString("latest_version","");
                        String vrsn_desc = device_info.getString("version_desc","");
                        String vrsn_link = device_info.getString("version_link","");
                        if(!versionName.equals(latest_vrsn)){
                            update_app(latest_vrsn,vrsn_desc,vrsn_link);
                        } else {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(splash.this, login.class));
                                    finish();
                                }
                            }, 3000);
                        }
                    }else {
                        check_app(versionName);
                    }
                } else {

                    check_app(versionName);
                }
        }

        /*a

        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.alert_noconnection);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            dialog.setCancelable(false);
            Button btTryAgain = dialog.findViewById(R.id.bt_try_again);

            btTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();
                }
            });

            dialog.show();
        } else {




        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splash.this, login.class));
                finish();
            }
        }, 3000);
        }
        */

    }

    private void check_app(final String version) {
        String url = "https://pastebin.com/raw/3ZbZ4jg0";


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] arrayString = response.split(";");
                String server_stats = arrayString[0];
                String latest_version = arrayString[1];
                String version_desc = arrayString[2];
                final String version_link = arrayString[3];
                String error_desc = arrayString[4];
                if(server_stats.equals("ok")) {
                    if (version.equals(latest_version)) {

                        if (Objects.equals(device_info.getString("device_key", ""), "")) {
                            gen_id(android.os.Build.BRAND,android.os.Build.MODEL);

                            SharedPreferences save_preferences = getSharedPreferences("covid_preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = save_preferences.edit();
                            editor.putString(getResources().getString(R.string.pref_stats_date),"0");
                            editor.putString(getResources().getString(R.string.pref_cases),"0");
                            editor.putString(getResources().getString(R.string.pref_recovered),"0");
                            editor.putString(getResources().getString(R.string.pref_deaths),"0");
                            editor.putString(getResources().getString(R.string.pref_active),"0");
                            editor.apply();

                        }else if(!Objects.equals(device_info.getString("device_key", ""), "")){
                            startActivity(new Intent(splash.this, login.class));
                            finish();

                        }

                        SharedPreferences.Editor editor = device_info.edit();
                        editor.putString("latest_version",latest_version);
                        editor.putString("version_desc",version_desc);
                        editor.putString("version_link",version_link);
                        editor.apply();
                    } else {
                        update_app(latest_version,version_desc,version_link);
                    }
                }else if(server_stats.equals("error")){

                    // Build an AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);

                    // Set a title for alert dialog
                    builder.setTitle("Server Error");

                    // Ask the final question
                    builder.setMessage(error_desc);

                    // Set click listener for alert dialog buttons
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    // User clicked the Yes button
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com")));
                                    break;

                                case DialogInterface.BUTTON_NEUTRAL:
                                    // Neutral/Cancel button clicked
                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                    intent.addCategory(Intent.CATEGORY_HOME);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    };

                    // Set the alert dialog yes button click listener
                    builder.setPositiveButton("Ok", dialogClickListener);

                    // Set the alert dialog cancel/neutral button click listener
                    builder.setNeutralButton("Exit", dialogClickListener);

                    builder.setCancelable(false);
                    AlertDialog dialog = builder.create();
                    // Display the three buttons alert dialog on interface
                    dialog.show();


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //On Error
                check_net();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setShouldCache(false);
        requestQueue.add(request);

    }

    private void update_app(String latest_version, String version_desc, final String version_link) {


        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);

        // Set a title for alert dialog
        builder.setTitle("Please Update");

        // Ask the final question
        builder.setMessage("Version: " +latest_version + "\n\n" + version_desc);

        // Set click listener for alert dialog buttons
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // User clicked the Yes button
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(version_link)));
                        break;

                    case DialogInterface.BUTTON_NEUTRAL:
                        // Neutral/Cancel button clicked
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                        break;
                }
            }
        };

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Update", dialogClickListener);

        // Set the alert dialog cancel/neutral button click listener
        builder.setNeutralButton("Exit", dialogClickListener);

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        // Display the three buttons alert dialog on interface
        dialog.show();

        SharedPreferences.Editor editor = device_info.edit();
        editor.putString("latest_version",latest_version);
        editor.putString("version_desc",version_desc);
        editor.putString("version_link",version_link);
        editor.apply();
    }

    private void check_net() {



        if(!Objects.equals(device_info.getString("device_key", ""), "")){
            Toast.makeText(splash.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(splash.this, login.class));
            finish();
        }else{

            // Build an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);

            // Set a title for alert dialog
            builder.setTitle("No Internet Connection");

            // Set click listener for alert dialog buttons
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // User clicked the Yes button
                            Intent openWirelessSettings = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(openWirelessSettings);
                            break;

                        case DialogInterface.BUTTON_NEUTRAL:
                            // Neutral/Cancel button clicked
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            startActivity(intent);
                            break;
                    }
                }
            };

            // Set the alert dialog yes button click listener
            builder.setPositiveButton("Connect", dialogClickListener);

            // Set the alert dialog cancel/neutral button click listener
            builder.setNeutralButton("Exit", dialogClickListener);

            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            // Display the three buttons alert dialog on interface
            dialog.show();
        }
    }

    private void gen_id(final String device_brand, final String device_model){

        String uRl = "https://bastaleakserver000.000webhostapp.com/QPass_App/device_gen_id.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] arrayString = response.split(";");
                String status = arrayString[0];
                String key = arrayString[1];

                if(status.equals("Success")){


                    SharedPreferences.Editor editor = device_info.edit();
                    editor.putString("device_key",key);
                    editor.apply();

                    startActivity(new Intent(splash.this, login.class));
                    finish();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Build an AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);

                // Set a title for alert dialog
                builder.setTitle("Server Error");

                // Ask the final question
                builder.setMessage("Please Contact Support.");

                // Set click listener for alert dialog buttons
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                // User clicked the Yes button
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com")));
                                break;

                            case DialogInterface.BUTTON_NEUTRAL:
                                // Neutral/Cancel button clicked
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                startActivity(intent);
                                break;
                        }
                    }
                };

                // Set the alert dialog yes button click listener
                builder.setPositiveButton("Ok", dialogClickListener);

                // Set the alert dialog cancel/neutral button click listener
                builder.setNeutralButton("Exit", dialogClickListener);

                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                // Display the three buttons alert dialog on interface
                dialog.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("device_brand", device_brand);
                param.put("device_model", device_model);
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        MySingleton.getInstance(splash.this).addToRequestQueue(request);

    }


    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        startActivity(new Intent(splash.this,splash.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();

    }


}