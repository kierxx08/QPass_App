package com.kierasis.qpasslaurel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    private long backPressedTime;
    private Toast backToast;

    MaterialEditText username,password;
    Button login, register;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        SharedPreferences device_info = getSharedPreferences("device-info", MODE_PRIVATE);
        final String key = device_info.getString("device_key", "");


        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this,register.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtUsername = username.getText().toString();
                String txtPassword = password.getText().toString();


                if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(login.this, "All Fields required", Toast.LENGTH_SHORT).show();
                }else{
                    login(txtUsername,txtPassword,key);
                }
            }
        });

        String loginStatus = sharedPreferences.getString(getResources().getString(R.string.prefLoginState),"");

        if(loginStatus.equals("loggedin")){
            startActivity(new Intent(login.this,MainActivity.class));
            finish();
        }
    }

    private void login(final String username, final String password, final String device_id){
        final ProgressDialog progressDialog = new ProgressDialog(login.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://bastaleakserver000.000webhostapp.com/QPass_App/login.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Login Success")){
                    progressDialog.dismiss();
                    String[] arrayString = response.split(";");
                    String key = arrayString[1];
                    String user_id = arrayString[2];
                    String user_fname = arrayString[3];
                    String user_lname = arrayString[4];
                    String user_bday = arrayString[5];
                    String user_mobile = arrayString[6];
                    String user_brgy = arrayString[7];
                    String user_add2 = arrayString[8];
                    String user_gender = arrayString[9];
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getResources().getString(R.string.prefLoginState),"loggedin");
                    editor.putString(getResources().getString(R.string.prefLoginKey),key);
                    editor.putString("user_name",username);
                    editor.putString("key2",user_id);
                    editor.putString("user_fname",user_fname);
                    editor.putString("user_lname",user_lname);
                    editor.putString("user_bday",user_bday);
                    editor.putString("user_mobile",user_mobile);
                    editor.putString("user_brgy",user_brgy);
                    editor.putString("user_add2",user_add2);
                    editor.putString("user_gender",user_gender);
                    editor.apply();

                    Toast.makeText(login.this, "Login Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(login.this,MainActivity.class));
                    finish();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(login.this, response, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if((error.toString().contains("html"))||(error.toString().contains("<!DOCTYPE html>"))){
                    Toast.makeText(login.this, "App Expired. Please Update.", Toast.LENGTH_SHORT).show();
                }else if(error.toString().contains("end of stream")){
                    Toast.makeText(login.this, "Runtime Timeout", Toast.LENGTH_SHORT).show();
                }else if (error.toString().contains("Unable to resolve host")) {
                    Toast.makeText(login.this, "Server Error. Please Try Again.", Toast.LENGTH_SHORT).show();
                    }else if(error.toString().contains("NoConnectionError")) {
                    //    Toast.makeText(MainActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(login.this, error.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("username", username);
                param.put("psw", password);
                param.put("device_id", device_id);
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(login.this).addToRequestQueue(request);

    }

    @Override
    public void onBackPressed() {
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