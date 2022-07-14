package com.kierasis.qpasslaurel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class case_list extends AppCompatActivity implements case_adapter.OnNoteListener {
    RecyclerView recyclerView;
    List<case_ext> cases;

    private  static String JSON_URL = "https://bastaleakserver000.000webhostapp.com/QPass_App/cases_list_json.php";
    case_adapter adapter;

    SimpleArcLoader simpleArcLoader3;

    SwipeRefreshLayout refresh;
    ImageView no_net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_list);

        recyclerView = findViewById(R.id.casesList);
        cases = new ArrayList<>();
        extractSongs();


        simpleArcLoader3 = findViewById(R.id.loader3);
        simpleArcLoader3.start();

        refresh = findViewById(R.id.refresh_case_list);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                startActivity(new Intent(case_list.this,case_list.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();


            }
        });

        no_net = findViewById(R.id.no_net_case_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cl);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void extractSongs() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject caseObject = response.getJSONObject(i);
                        case_ext case2 = new case_ext();
                        case2.setTitle(caseObject.getString("case_id").toString());
                        case2.setArtist(caseObject.getString("case_brgy").toString());
                        case2.setCoverImage(caseObject.getString("case_image").toString());
                        case2.setSongURL(caseObject.getString("url").toString());
                        cases.add(case2);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    simpleArcLoader3.stop();
                    simpleArcLoader3.setVisibility(View.GONE);
                    no_net.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                }

                recyclerView.setLayoutManager(new LinearLayoutManager(case_list.this));
                adapter = new case_adapter(case_list.this,cases,case_list.this);
                recyclerView.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                simpleArcLoader3.stop();
                simpleArcLoader3.setVisibility(View.GONE);
                no_net.setVisibility(View.VISIBLE);
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        });

        queue.add(jsonArrayRequest);
    }

    @Override
    public void onNoteClick(int position) {
        Log.d("tag","onNoteClick: clicked.");

        Intent intent = new Intent(case_list.this, case_view.class);
        intent.putExtra("code",String.valueOf(position));
        startActivity(intent);
    }
}