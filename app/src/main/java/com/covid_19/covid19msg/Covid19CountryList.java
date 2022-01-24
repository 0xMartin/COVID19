package com.covid_19.covid19msg;

import android.util.Log;

import com.covid_19.module.Communication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Covid19CountryList extends Communication.Message {

    public static class Country {
        public String code;
        public String name;
    }

    public List<Covid19CountryList.Country> countries;

    public Covid19CountryList() {
        this.countries = new ArrayList<Country>();
    }

    @Override
    public String getAPIURL() {
        return "https://covid-19-data.p.rapidapi.com/help/countries";
    }

    @Override
    public void parseJSON(String json) {
        try {

            JSONArray array = new JSONArray(json);
            for(int i = 0; i < array.length(); ++i) {
                JSONObject jObject = array.getJSONObject(i);
                Covid19CountryList.Country data = new Covid19CountryList.Country();
                data.code = jObject.getString("alpha2code");
                data.name = jObject.getString("name");
                this.countries.add(data);
            }

        } catch (JSONException e) {
            Log.d("Covid19CountryData Exception", e.getMessage());
        }
    }

    @Override
    public void error() {

    }

}
