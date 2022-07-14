package com.kierasis.qpasslaurel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class tracker extends AppCompatActivity {
    TextView tvCases,tvRecovered,tvDeaths,tvActive,tvUpdate;
    SimpleArcLoader simpleArcLoader,simpleArcLoader2;
    ScrollView scrollView;
    PieChart pieChart;
    SharedPreferences save_preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        save_preferences = getSharedPreferences("covid_preferences", Context.MODE_PRIVATE);
        tvCases = findViewById(R.id.tvCases);
        tvRecovered = findViewById(R.id.tvRecovered);
        tvDeaths = findViewById(R.id.tvDeaths);
        tvActive = findViewById(R.id.tvActive);
        tvUpdate = findViewById(R.id.tvUpdate);

        simpleArcLoader = findViewById(R.id.loader);
        simpleArcLoader2 = findViewById(R.id.loader2);
        scrollView = findViewById(R.id.scrollStats);
        pieChart = findViewById(R.id.piechart);

        fetchData();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_trckr);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchData() {
        String url = "https://bastaleakserver000.000webhostapp.com/QPass_App/api_laurel_covid.php";

        simpleArcLoader.start();
        simpleArcLoader2.start();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());

                            tvUpdate.setText(jsonObject.getString("updated"));
                            tvCases.setText(jsonObject.getString("cases"));
                            tvRecovered.setText(jsonObject.getString("recovered"));
                            tvDeaths.setText(jsonObject.getString("deaths"));
                            tvActive.setText(jsonObject.getString("active"));

                            SharedPreferences.Editor editor = save_preferences.edit();
                            editor.putString(getResources().getString(R.string.pref_stats_date),jsonObject.getString("updated"));
                            editor.putString(getResources().getString(R.string.pref_cases),jsonObject.getString("cases"));
                            editor.putString(getResources().getString(R.string.pref_recovered),jsonObject.getString("recovered"));
                            editor.putString(getResources().getString(R.string.pref_deaths),jsonObject.getString("deaths"));
                            editor.putString(getResources().getString(R.string.pref_active),jsonObject.getString("active"));
                            editor.apply();

                            pieChart.addPieSlice(new PieModel("Cases",Integer.parseInt(tvCases.getText().toString()), Color.parseColor("#ffa726")));
                            pieChart.addPieSlice(new PieModel("Recovered",Integer.parseInt(tvRecovered.getText().toString()), Color.parseColor("#66bb6a")));
                            pieChart.addPieSlice(new PieModel("Deaths",Integer.parseInt(tvDeaths.getText().toString()), Color.parseColor("#ef5350")));
                            pieChart.addPieSlice(new PieModel("Active",Integer.parseInt(tvActive.getText().toString()), Color.parseColor("#2986f6")));



                            pieChart.startAnimation();

                            simpleArcLoader.stop();
                            simpleArcLoader2.stop();
                            simpleArcLoader.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                            simpleArcLoader2.setVisibility(View.GONE);
                            pieChart.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();

                            String stats_date = save_preferences.getString(getResources().getString(R.string.pref_stats_date), "");
                            String cases = save_preferences.getString(getResources().getString(R.string.pref_cases), "");
                            String recovered = save_preferences.getString(getResources().getString(R.string.pref_recovered), "");
                            String deaths = save_preferences.getString(getResources().getString(R.string.pref_deaths), "");
                            String active = save_preferences.getString(getResources().getString(R.string.pref_active), "");

                            tvUpdate.setText(stats_date);
                            tvCases.setText(cases);
                            tvRecovered.setText(recovered);
                            tvDeaths.setText(deaths);
                            tvActive.setText(active);

                            pieChart.addPieSlice(new PieModel("Cases",Integer.parseInt(cases), Color.parseColor("#ffa726")));
                            pieChart.addPieSlice(new PieModel("Recovered",Integer.parseInt(recovered), Color.parseColor("#66bb6a")));
                            pieChart.addPieSlice(new PieModel("Deaths",Integer.parseInt(deaths), Color.parseColor("#ef5350")));
                            pieChart.addPieSlice(new PieModel("Active",Integer.parseInt(active), Color.parseColor("#2986f6")));

                            pieChart.startAnimation();

                            simpleArcLoader.stop();
                            simpleArcLoader2.stop();
                            simpleArcLoader.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                            simpleArcLoader2.setVisibility(View.GONE);
                            pieChart.setVisibility(View.VISIBLE);

                            //Toast.makeText(tracker.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(tracker.this, "Can't connect to the Server.\n(eg. No Internet Connection)", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                String stats_date = save_preferences.getString(getResources().getString(R.string.pref_stats_date), "");
                String cases = save_preferences.getString(getResources().getString(R.string.pref_cases), "");
                String recovered = save_preferences.getString(getResources().getString(R.string.pref_recovered), "");
                String deaths = save_preferences.getString(getResources().getString(R.string.pref_deaths), "");
                String active = save_preferences.getString(getResources().getString(R.string.pref_active), "");

                tvUpdate.setText(stats_date);
                tvCases.setText(cases);
                tvRecovered.setText(recovered);
                tvDeaths.setText(deaths);
                tvActive.setText(active);

                pieChart.addPieSlice(new PieModel("Cases",Integer.parseInt(cases), Color.parseColor("#ffa726")));
                pieChart.addPieSlice(new PieModel("Recovered",Integer.parseInt(recovered), Color.parseColor("#66bb6a")));
                pieChart.addPieSlice(new PieModel("Deaths",Integer.parseInt(deaths), Color.parseColor("#ef5350")));
                pieChart.addPieSlice(new PieModel("Active",Integer.parseInt(active), Color.parseColor("#2986f6")));

                pieChart.startAnimation();

                simpleArcLoader.stop();
                simpleArcLoader2.stop();
                simpleArcLoader.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                simpleArcLoader2.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);
                //Toast.makeText(tracker.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(tracker.this, "Can't connect to the Server.\n(eg. No Internet Connection)", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }
}