package com.kierasis.qpasslaurel;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class register extends AppCompatActivity {

    MaterialEditText fname, lname, userName, mobile, add, password;
    RadioGroup radioGroup;
    Button register;
    Spinner brgy;
    ArrayList<String> arrayList_brgy;
    ArrayAdapter<String> arrayAdapter_brgy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.username);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        mobile = findViewById(R.id.mobile);
        brgy = findViewById(R.id.barangay);
        add = findViewById(R.id.address2);
        arrayList_brgy=new ArrayList<>();
        arrayList_brgy.add("Please Choose");
        arrayList_brgy.add("As-is");
        arrayList_brgy.add("Balakilong");
        arrayList_brgy.add("Berinayan");
        arrayList_brgy.add("Bugaan East");
        arrayList_brgy.add("Bugaan West");
        arrayList_brgy.add("Buso‑buso");
        arrayList_brgy.add("Dayap Itaas");
        arrayList_brgy.add("Gulod");
        arrayList_brgy.add("Leviste");
        arrayList_brgy.add("Molinete");
        arrayList_brgy.add("Niyugan");
        arrayList_brgy.add("Paliparan");
        arrayList_brgy.add("Poblacion 1");
        arrayList_brgy.add("Poblacion 2");
        arrayList_brgy.add("Poblacion 3");
        arrayList_brgy.add("Poblacion 4");
        arrayList_brgy.add("Poblacion 5");
        arrayList_brgy.add("San Gabriel");
        arrayList_brgy.add("San Gregorio");
        arrayList_brgy.add("Santa Maria");
        arrayList_brgy.add("Ticub");
        arrayAdapter_brgy=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList_brgy);
        brgy.setAdapter(arrayAdapter_brgy);
        final Calendar myCalendar = Calendar.getInstance();

        final EditText bday = (EditText) findViewById(R.id.Birthday);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                    String myFormat = "MM/dd/yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                bday.setText(sdf.format(myCalendar.getTime()));
            }

        };

        bday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(register.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        password = findViewById(R.id.password);
        radioGroup = findViewById(R.id.radiogp);
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtUsername = userName.getText().toString();
                String txtFname= fname.getText().toString();
                String txtLname= lname.getText().toString();
                String txtBday= bday.getText().toString();
                String txtMobile = mobile.getText().toString();
                String txtBrgy= brgy.getSelectedItem().toString();
                String txtAdd= add.getText().toString();
                String txtPassword = password.getText().toString();
                if(TextUtils.isEmpty(txtUsername)  || TextUtils.isEmpty(txtFname) || TextUtils.isEmpty(txtLname)){
                    Toast.makeText(register.this,"All Fields Required", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(txtBday)){
                    Toast.makeText(register.this,"Please Select your Birthday", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(txtMobile)){
                    Toast.makeText(register.this,"All Fields Required", Toast.LENGTH_SHORT).show();
                }else if(txtBrgy.equals("Please Choose")){
                    Toast.makeText(register.this,"Please Choose Barangay", Toast.LENGTH_SHORT).show();

                }else if(TextUtils.isEmpty(txtAdd) || TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(register.this,"All Fields Required", Toast.LENGTH_SHORT).show();
                }else{
                    int genderId = radioGroup.getCheckedRadioButtonId();
                    RadioButton selected_Gender = radioGroup.findViewById(genderId);
                    if (selected_Gender == null){
                        Toast.makeText(register.this,"Please Select Gender", Toast.LENGTH_SHORT).show();
                    }else{
                        String selectGender = selected_Gender.getText().toString();

                        registerNewAccount(txtUsername,txtFname,txtLname,txtBday,txtMobile,txtBrgy,txtAdd,txtPassword,selectGender);
                    }


                }

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_reg);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void  registerNewAccount(final String username, final String fname, final String lname, final String bday, final String mobile, final String brgy, final String add, final String password, final String gender){
        final ProgressDialog progressDialog = new ProgressDialog(register.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Registering New Account");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://bastaleakserver000.000webhostapp.com/QPass_App/register.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Successfully Registered")){
                    progressDialog.dismiss();
                    Toast.makeText(register.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(register.this,login.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(register.this, response, Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if((error.toString().contains("html"))||(error.toString().contains("<!DOCTYPE html>"))){
                    Toast.makeText(register.this, "Error 101: App Expired. Please Update.", Toast.LENGTH_SHORT).show();
                }else if(error.toString().contains("end of stream")){
                    Toast.makeText(register.this, "Error 102: Runtime Timeout", Toast.LENGTH_SHORT).show();
                }else if(error.toString().contains("SSLHandshakeException")){
                    Toast.makeText(register.this, "Error 103: Server Error. Please Try Again.", Toast.LENGTH_SHORT).show();
                }else if (error.toString().contains("Unable to resolve host")) {
                    Toast.makeText(register.this, "Error 104: Server Error. Please Try Again. ", Toast.LENGTH_SHORT).show();
                    //}else if(error.toString().contains("NoConnectionError")) {
                    //   Toast.makeText(register.this, "No Connection", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(register.this, "Error 105: "+error.toString(), Toast.LENGTH_SHORT).show();
                }


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("username", username);
                param.put("fname", fname);
                param.put("lname", lname);
                param.put("bday", bday);
                param.put("mobile", mobile);
                param.put("brgy", brgy);
                param.put("add", add);
                param.put("psw", password);
                param.put("gender", gender);
                return param;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(register.this).addToRequestQueue(request);



    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(register.this,login.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}