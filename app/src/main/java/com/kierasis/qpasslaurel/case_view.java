package com.kierasis.qpasslaurel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.HashMap;
import java.util.Map;

public class case_view extends AppCompatActivity {
    String caseid;
    TextView caseNo, caseAge, caseGender, caseBrgy, caseStatus, caseWhere, caseDetected;
    SimpleArcLoader loader;
    ScrollView svCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_view);

        caseNo = findViewById(R.id.tvCaseNo);
        caseAge = findViewById(R.id.tvCaseAge);
        caseGender = findViewById(R.id.tvCaseGender);
        caseBrgy = findViewById(R.id.tvCaseBrgy);
        caseStatus = findViewById(R.id.tvCaseStatus);
        caseWhere = findViewById(R.id.tvCaseWhere);
        caseDetected = findViewById(R.id.tvCaseDetected);
        loader = findViewById(R.id.loader4);
        svCase= findViewById(R.id.caseView);

        Intent intent = getIntent();
        caseid = intent.getStringExtra("code");
        caseView(caseid);

        loader.start();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cv);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void caseView(final String caseid){

        String uRl = "https://bastaleakserver000.000webhostapp.com/QPass_App/case_view.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Success")){

                    String[] arrayString = response.split(";");
                    String caseID = arrayString[1];
                    String caseAGE = arrayString[2];
                    String caseGNDR = arrayString[3];
                    String caseBRGY = arrayString[4];
                    String caseSTATUS = arrayString[5];
                    String caseWHERE = arrayString[6];
                    String caseDETECTED = arrayString[7];

                    caseNo.setText(caseID);
                    caseAge.setText(caseAGE);
                    caseGender.setText(caseGNDR);
                    caseBrgy.setText(caseBRGY);
                    caseStatus.setText(caseSTATUS);
                    caseWhere.setText(caseWHERE);
                    caseDetected.setText(caseDETECTED);

                    loader.stop();
                    svCase.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);


                }else {

                    Toast.makeText(case_view.this, response, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if((error.toString().contains("html"))||(error.toString().contains("<!DOCTYPE html>"))){
                    Toast.makeText(case_view.this, "App Expired. Please Update.", Toast.LENGTH_SHORT).show();
                }else if(error.toString().contains("end of stream")){
                    Toast.makeText(case_view.this, "Runtime Timeout", Toast.LENGTH_SHORT).show();
                }else if (error.toString().contains("Unable to resolve host")) {
                    Toast.makeText(case_view.this, "Server Error. Please Try Again.", Toast.LENGTH_SHORT).show();
                    //}else if(error.toString().contains("NoConnectionError")) {
                    //    Toast.makeText(MainActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(case_view.this, error.toString(), Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(case_view.this).addToRequestQueue(request);

    }
}