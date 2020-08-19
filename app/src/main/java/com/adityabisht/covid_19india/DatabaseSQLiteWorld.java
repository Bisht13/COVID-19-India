package com.adityabisht.covid_19india;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

public class DatabaseSQLiteWorld extends SQLiteOpenHelper {
    private static final String dbName = "db";
    private static final int ver = 1;
    JSONArray data;

    public DatabaseSQLiteWorld(Context context) {
        super(context, dbName, null, ver);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE DATAWORLD (country TEXT PRIMARY KEY, countrycode TEXT, active REAL, confirmed REAL, deaths REAL, deltaconfirmed REAL, deltadeaths REAL, deltarecovered REAL, recovered REAL)";
        sqLiteDatabase.execSQL(sql);
        for (int i=0;i<data.length();i++){
            try {
                insertData(data.getJSONObject(i).getString("Country"),
                        data.getJSONObject(i).getString("CountryCode"),
                        Integer.parseInt(data.getJSONObject(i).getString("TotalConfirmed"))-Integer.parseInt(data.getJSONObject(i).getString("TotalDeaths"))-Integer.parseInt(data.getJSONObject(i).getString("TotalRecovered")),
                        Integer.parseInt(data.getJSONObject(i).getString("TotalConfirmed")),
                        Integer.parseInt(data.getJSONObject(i).getString("TotalDeaths")),
                        Integer.parseInt(data.getJSONObject(i).getString("NewConfirmed")),
                        Integer.parseInt(data.getJSONObject(i).getString("NewDeaths")),
                        Integer.parseInt(data.getJSONObject(i).getString("NewRecovered")),
                        Integer.parseInt(data.getJSONObject(i).getString("TotalRecovered")),
                        sqLiteDatabase);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertData(String country, String countrycode, int active, int confirmed, int deaths, int deltaconfirmed, int deltadeaths, int deltarecovered, int recovered, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put("country", country);
        values.put("countrycode", countrycode);
        values.put("active", active);
        values.put("confirmed", confirmed);
        values.put("deaths", deaths);
        values.put("deltaconfirmed", deltaconfirmed);
        values.put("deltadeaths", deltadeaths);
        values.put("deltarecovered", deltarecovered);
        values.put("recovered", recovered);
        db.insert("DATAWORLD",null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
