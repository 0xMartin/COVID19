package com.covid_19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.covid_19.covid19msg.Covid19CountryData;
import com.covid_19.module.Communication;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.type.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class StatisticsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String COUNTRY_CODE_KEY = "country_code";

    private String countryCode = "";
    private GoogleMap mMap = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //action bar
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.countryCode = extras.getString(StatisticsActivity.COUNTRY_CODE_KEY);
        } else {
            onBackPressed();
        }

        //update stats
        updateStats();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateStats() {
        final TextView textView_update_statistics = findViewById(R.id.textView_update_statistics);
        final TextView textView_country = findViewById(R.id.textView_country_statistics);
        final TextView textView_confirmed = findViewById(R.id.textView_confirmed_statistics);
        final TextView textView_recovered = findViewById(R.id.textView_recovered_statistics);
        final TextView textView_critical = findViewById(R.id.textView_critical_statistics);
        final TextView textView_deaths = findViewById(R.id.textView_deaths_statistics);
        final PieChart pieChart = findViewById(R.id.chart);

        final Covid19CountryData covid19CountryData = new Covid19CountryData(this.countryCode) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponseEvent(Communication.Message msg) {
                Covid19CountryData data = (Covid19CountryData) msg;

                //values
                String date = data.lastUpdate.replace("T", " ");
                date = date.substring(0, date.indexOf("+"));
                textView_update_statistics.setText(getResources().getString(R.string.last_update) + ": " + date);
                textView_country.setText(data.country + " " + AppGlobal.getFlagEmoji(data.countyCode));
                textView_confirmed.setText(AppGlobal.getFormatedNumber(data.confirmed));
                textView_recovered.setText(AppGlobal.getFormatedNumber(data.recovered));
                textView_critical.setText(AppGlobal.getFormatedNumber(data.critical));
                textView_deaths.setText(AppGlobal.getFormatedNumber(data.deaths));

                //pie graph
                List<PieEntry> pieEntires = new ArrayList<>();
                pieEntires.add(new PieEntry(data.recovered, "Recovered"));
                pieEntires.add(new PieEntry(data.critical, "Critical"));
                pieEntires.add(new PieEntry(data.confirmed, "Confirmed"));
                pieEntires.add(new PieEntry(data.deaths, "Deaths"));
                PieDataSet dataSet = new PieDataSet(pieEntires,"");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                PieData gdata = new PieData(dataSet);
                gdata.setValueTextSize(14f);
                //get the pieChart
                pieChart.setData(gdata);
                pieChart.setDrawHoleEnabled(false);
                pieChart.setEntryLabelTextSize(20f);
                pieChart.getDescription().setEnabled(false);
                pieChart.getLegend().setEnabled(false);
                pieChart.animateXY(1000, 1000);
                pieChart.setContentDescription("");
                pieChart.invalidate();

                //map
                setMapPosition(data);
            }
        };
        AppGlobal.communication.fetch(covid19CountryData);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void setMapPosition(Covid19CountryData data) {
        if(mMap == null) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setMapPosition(data);
                }
            }, 200);
        } else {
            LatLng pos = new LatLng(data.latitude, data.longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(data.country));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        }
    }

}