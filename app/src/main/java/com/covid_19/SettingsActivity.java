package com.covid_19;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switch_update, switch_confirmed, switch_recovered, switch_critical, switch_deaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //action bar
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        //update switch status
        switch_update = findViewById(R.id.switch_update);
        switch_update.setChecked(AppGlobal.Setting.updateTime_visible);
        switch_update.setOnCheckedChangeListener((compoundButton, b) -> saveChanges());

        switch_confirmed = findViewById(R.id.switch_confirmed);
        switch_confirmed.setChecked(AppGlobal.Setting.confirmed_visible);
        switch_confirmed.setOnCheckedChangeListener((compoundButton, b) -> saveChanges());

        switch_recovered = findViewById(R.id.switch_recovered);
        switch_recovered.setChecked(AppGlobal.Setting.recovered_visible);
        switch_recovered.setOnCheckedChangeListener((compoundButton, b) -> saveChanges());

        switch_critical = findViewById(R.id.switch_critical);
        switch_critical.setChecked(AppGlobal.Setting.critical_visible);
        switch_critical.setOnCheckedChangeListener((compoundButton, b) -> saveChanges());

        switch_deaths = findViewById(R.id.switch_deaths);
        switch_deaths.setChecked(AppGlobal.Setting.deaths_visible);
        switch_deaths.setOnCheckedChangeListener((compoundButton, b) -> saveChanges());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveChanges() {
        //update changes
        AppGlobal.Setting.updateTime_visible = switch_update.isChecked();
        AppGlobal.Setting.confirmed_visible = switch_confirmed.isChecked();
        AppGlobal.Setting.recovered_visible = switch_recovered.isChecked();
        AppGlobal.Setting.critical_visible = switch_critical.isChecked();
        AppGlobal.Setting.deaths_visible = switch_deaths.isChecked();

        //save to local storage
        AppGlobal.storeSettings();
    }

}