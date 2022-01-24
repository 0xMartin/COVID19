package com.covid_19;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.covid_19.covid19msg.Covid19CountryList;
import com.covid_19.module.Communication;

import java.util.List;
import java.util.Objects;

public class GlobalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);

        //action bar
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        //countries
        Covid19CountryList covid19CountryAll = new Covid19CountryList() {
            @Override
            public void onResponseEvent(Communication.Message msg) {
                Covid19CountryList data = (Covid19CountryList) msg;
                updateListData(data.countries);
            }
        };
        AppGlobal.communication.fetch(covid19CountryAll);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateListData(List<Covid19CountryList.Country> countryDataList) {
        //listview
        ArrayAdapter<Covid19CountryList.Country> adapter = new ArrayAdapter<Covid19CountryList.Country>(
                this, R.layout.item_country, countryDataList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_country, parent, false);
                }
                Covid19CountryList.Country country = getItem(position);
                final TextView flag = (TextView) convertView.findViewById(R.id.flag);
                final TextView name = (TextView) convertView.findViewById(R.id.name);
                flag.setText(AppGlobal.getFlagEmoji(country.code));
                name.setText(country.name);
                return convertView;
            }
        };
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

}