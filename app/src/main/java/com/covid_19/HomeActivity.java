package com.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.covid_19.covid19msg.Covid19CountryData;
import com.covid_19.module.Communication;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).hide();

        //bg animation
        final FrameLayout frameLayout = findViewById(R.id.background_home);
        if(AppGlobal.animation != null) {
            frameLayout.addView(AppGlobal.animation, 0);
        }


        Button btn;

        //button_statistics
        btn = (Button) findViewById(R.id.button_statistics);
        btn.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, StatisticsActivity.class);
            intent.putExtra(StatisticsActivity.COUNTRY_CODE_KEY, AppGlobal.Setting.homeCountryCode);
            startActivity(intent);
        });

        //button_global
        btn = (Button) findViewById(R.id.button_global);
        btn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GlobalActivity.class);
            startActivity(intent);
        });

        //button_settings
        btn = (Button) findViewById(R.id.button_settings);
        btn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppGlobal.animation.run();

        //home country stats
        updateStats();

        //(Settings) apply visibility
        final TextView textView_update = findViewById(R.id.textView_update);
        textView_update.setVisibility(AppGlobal.Setting.updateTime_visible ? View.VISIBLE : View.GONE);

        final TableRow tableRow_confirmed = findViewById(R.id.tableRow_confirmed);
        tableRow_confirmed.setVisibility(AppGlobal.Setting.confirmed_visible ? View.VISIBLE : View.GONE);

        final TableRow tableRow_recovered = findViewById(R.id.tableRow_recovered);
        tableRow_recovered.setVisibility(AppGlobal.Setting.recovered_visible ? View.VISIBLE : View.GONE);

        final TableRow tableRow_critical = findViewById(R.id.tableRow_critical);
        tableRow_critical.setVisibility(AppGlobal.Setting.critical_visible ? View.VISIBLE : View.GONE);

        final TableRow tableRow_deaths = findViewById(R.id.tableRow_deaths);
        tableRow_deaths.setVisibility(AppGlobal.Setting.deaths_visible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppGlobal.animation.stop();
    }

    public void updateStats() {
        final TextView textView_country = findViewById(R.id.textView_country);
        final TextView textView_confirmed = findViewById(R.id.textView_confirmed);
        final TextView textView_update = findViewById(R.id.textView_update);
        final TextView textView_recovered = findViewById(R.id.textView_recovered);
        final TextView textView_critical = findViewById(R.id.textView_critical);
        final TextView textView_deaths = findViewById(R.id.textView_deaths);

        Covid19CountryData data = new Covid19CountryData(AppGlobal.Setting.homeCountryCode) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponseEvent(Communication.Message msg) {
                Covid19CountryData data = (Covid19CountryData) msg;
                textView_country.setText(data.country + " " + AppGlobal.getFlagEmoji(data.countyCode));
                String date = data.lastUpdate.replace("T", " ");
                date = date.substring(0, date.indexOf("+"));
                textView_update.setText(getResources().getString(R.string.last_update) + ": " + date);
                textView_confirmed.setText(AppGlobal.getFormatedNumber(data.confirmed));
                textView_recovered.setText(AppGlobal.getFormatedNumber(data.recovered));
                textView_critical.setText(AppGlobal.getFormatedNumber(data.critical));
                textView_deaths.setText(AppGlobal.getFormatedNumber(data.deaths));
            }
        };
        AppGlobal.communication.fetch(data);
    }

}