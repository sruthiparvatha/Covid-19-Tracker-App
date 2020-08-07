package com.rsruthiparvatha.covid19_app;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.List;

public class AffectedCountries extends AppCompatActivity {

    EditText edtSearch;
    SimpleArcLoader simpleArcLoader;
    ListView listView;

    public static List<CountryModel> countryModelList = new ArrayList<>();
    CountryModel countryModel;
    MyCustomAdapter myCustomAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affected_countries);

        edtSearch = findViewById(R.id.edtSearch);
        simpleArcLoader = findViewById(R.id.loader);
        listView = findViewById(R.id.listView);

        //to set back button, and title on app action bar

        getSupportActionBar().setTitle("Affected Countries");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //fetch Data from API
        fetchData();

        //For getting details on clicking on countries in listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), DetailActivity.class).putExtra("position",position));
            }
        });

        //for search bar aka editText
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myCustomAdapter.getFilter().filter(s);
                myCustomAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchData() {
        //Fetch data using REST API
        String url = "https://corona.lmao.ninja/v2/countries";
        simpleArcLoader.start();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String countryName = jsonObject.getString("country");
                                    String cases =jsonObject.getString("cases") ;
                                    String todayCases = jsonObject.getString("todayCases");
                                    String deaths = jsonObject.getString("deaths");
                                    String todayDeaths = jsonObject.getString("todayDeaths");
                                    String recovered = jsonObject.getString("recovered");
                                    String todayRecovered = jsonObject.getString("todayRecovered");
                                    String active = jsonObject.getString("active");
                                    String critical = jsonObject.getString("critical") ;
                                    String population = jsonObject.getString("population") ;
                                    String tests = jsonObject.getString("tests");

                                    //for querying flag from nested object in json format
                                    JSONObject object = jsonObject.getJSONObject("countryInfo");
                                    String flagURL = object.getString("flag");

                                    //Store the data retrieved from API into CountryModel Object
                                    countryModel = new CountryModel(flagURL, countryName, cases, todayCases, deaths, todayDeaths, recovered, active, critical, tests, population);
                                    countryModelList.add(countryModel);
                            }

                            myCustomAdapter = new MyCustomAdapter(AffectedCountries.this,countryModelList);
                            listView.setAdapter(myCustomAdapter);
                            simpleArcLoader.stop();
                            simpleArcLoader.setVisibility(View.GONE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            simpleArcLoader.stop();
                            simpleArcLoader.setVisibility(View.GONE);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AffectedCountries.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }
}
