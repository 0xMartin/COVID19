package com.covid_19;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.covid_19.covid19msg.Covid19CountryData;
import com.covid_19.covid19msg.Covid19CountryList;
import com.covid_19.covid19msg.Covid19TotalData;
import com.covid_19.module.Communication;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GlobalActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView textView_update_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);

        //action bar
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        pieChart = findViewById(R.id.chart_total);
        textView_update_total = findViewById(R.id.textView_update_total);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //update country list
        AppGlobal.getInstance().communication.fetch(covid19CountryAll);

        //update pi graph (world stats total)
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppGlobal.getInstance().communication.fetch(covid19TotalData);

            }
        }, 1200);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    final Covid19CountryList covid19CountryAll = new Covid19CountryList() {
        @Override
        public void onResponseEvent(Communication.Message msg) {
            Covid19CountryList data = (Covid19CountryList) msg;
            try {
                updateListData(data.countries);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //store data
            AppGlobal.getInstance().dataStore.store("global_activity", data);
        }

        @Override
        public void error() throws ClassCastException {
            //load data
            Covid19CountryList data = new Covid19CountryList();
            if(AppGlobal.getInstance().dataStore.load("global_activity", data)) {
                try {
                    updateListData(data.countries);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void updateListData(List<Covid19CountryList.Country> countryDataList) throws Exception {
        //create list adapter
        ArrayAdapter<Covid19CountryList.Country> adapter = new ArrayAdapter<Covid19CountryList.Country>(
                this, R.layout.item_country, countryDataList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_country, parent, false);
                }
                Covid19CountryList.Country country = getItem(position);
                final TextView flag = (TextView) convertView.findViewById(R.id.flag_country_item);
                final TextView name = (TextView) convertView.findViewById(R.id.name_country_item);
                flag.setText(AppGlobal.getInstance().getFlagEmoji(country.code));
                name.setText(country.name);
                return convertView;
            }
        };

        //set adapter for listview
        ListView listView = findViewById(R.id.listView_statistics);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Covid19CountryList.Country country = adapter.getItem(i);

                Intent intent = new Intent(GlobalActivity.this, StatisticsActivity.class);
                intent.putExtra(StatisticsActivity.COUNTRY_CODE_KEY, country.code);
                startActivity(intent);
            }
        });
    }

    final Covid19TotalData covid19TotalData = new Covid19TotalData() {
        @Override
        public void onResponseEvent(Communication.Message msg) {
            Covid19TotalData data = (Covid19TotalData) msg;
            try {
                updatePieGraph(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //store data
            AppGlobal.getInstance().dataStore.store("global_activity_total", data);
        }

        @Override
        public void error() throws ClassCastException {
            //load data
            Covid19TotalData data = new Covid19TotalData();
            if(AppGlobal.getInstance().dataStore.load("global_activity_total", data)) {
                try {
                    updatePieGraph(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @SuppressLint("SetTextI18n")
    private void updatePieGraph(Covid19TotalData data) throws Exception {
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

        //last update
        String date = data.lastUpdate.replace("T", " ");
        date = date.substring(0, date.indexOf("+"));
        textView_update_total.setText(getResources().getString(R.string.last_update) + ": " + date);
    }

}