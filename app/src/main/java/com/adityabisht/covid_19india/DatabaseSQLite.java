package com.adityabisht.covid_19india;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.mikephil.charting.utils.EntryXComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class DatabaseSQLite extends SQLiteOpenHelper {
    private static final String dbName = "mydb";
    private static final int ver = 1;
    JSONArray data;
    JSONObject datatimeseries;

    public DatabaseSQLite(Context context) {
        super(context, dbName, null, ver);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        if (data!=null){
        String sql = "CREATE TABLE DATAINDIA (state TEXT PRIMARY KEY, statecode TEXT, active REAL, confirmed REAL, deaths REAL, deltaconfirmed REAL, deltadeaths REAL, deltarecovered REAL, recovered REAL)";
        sqLiteDatabase.execSQL(sql);

        //inserting values
        for (int i=0;i<38;i++){
            try {
                insertData(data.getJSONObject(i).getString("state"),
                        data.getJSONObject(i).getString("statecode"),
                        Integer.parseInt(data.getJSONObject(i).getString("active")),
                        Integer.parseInt(data.getJSONObject(i).getString("confirmed")),
                        Integer.parseInt(data.getJSONObject(i).getString("deaths")),
                        Integer.parseInt(data.getJSONObject(i).getString("deltaconfirmed")),
                        Integer.parseInt(data.getJSONObject(i).getString("deltadeaths")),
                        Integer.parseInt(data.getJSONObject(i).getString("deltarecovered")),
                        Integer.parseInt(data.getJSONObject(i).getString("recovered")),
                        sqLiteDatabase);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}

        if (datatimeseries!=null) {
            String sql = "CREATE TABLE TIMESERIES (date DATE PRIMARY KEY, confirmed REAL, deaths REAL, deltaconfirmed REAL, deltadeaths REAL, deltarecovered REAL, recovered REAL)";
            sqLiteDatabase.execSQL(sql);

            Iterator<String> iterator = datatimeseries.keys();
            while(iterator.hasNext()){
                String key = iterator.next();
                try{
                    JSONObject value = datatimeseries.getJSONObject(key);
                    Integer confirmed = null, deaths = null, deltaconfirmed = null, deltadeaths = null, deltarecovered = null, recovered = null;
                    JSONObject delta = value.getJSONObject("delta");
                    JSONObject total = value.getJSONObject("total");
                    try{
                        confirmed = Integer.parseInt(total.getString("confirmed"));
                    }catch (Exception e){
                    }
                    try{
                        deaths = Integer.parseInt(total.getString("deceased"));
                    }catch (Exception e){
                    }
                    try{
                        recovered = Integer.parseInt(total.getString("recovered"));
                    }catch (Exception e){
                    }
                    try{
                        deltaconfirmed = Integer.parseInt(delta.getString("confirmed"));
                    }catch (Exception e){
                    }
                    try{
                        deltadeaths = Integer.parseInt(delta.getString("deceased"));
                    }catch (Exception e){
                    }
                    try{
                        deltarecovered = Integer.parseInt(delta.getString("recovered"));
                    }catch (Exception e){
                    }
                    confirmed = confirmed==null?0:confirmed;
                    deaths = deaths==null?0:deaths;
                    deltaconfirmed = deltaconfirmed==null?0:deltaconfirmed;
                    deltadeaths = deltadeaths==null?0:deltadeaths;
                    deltarecovered = deltarecovered==null?0:deltarecovered;
                    recovered = recovered==null?0:recovered;
                    insertDataTimeSeries(key, confirmed, deaths, deltaconfirmed, deltadeaths, deltarecovered, recovered, sqLiteDatabase);
                }catch (JSONException e){
                    Log.d("myapp", "onCreate: Something went wrong");
                }
            }
        }
    }

    private void insertData(String state, String statecode, int active, int confirmed, int deaths, int deltaconfirmed, int deltadeaths, int deltarecovered, int recovered, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put("state", state);
        values.put("statecode", statecode);
        values.put("active", active);
        values.put("confirmed", confirmed);
        values.put("deaths", deaths);
        values.put("deltaconfirmed", deltaconfirmed);
        values.put("deltadeaths", deltadeaths);
        values.put("deltarecovered", deltarecovered);
        values.put("recovered", recovered);
        db.insert("DATAINDIA",null, values);
    }

    private void insertDataTimeSeries(String date, int confirmed, int deaths, int deltaconfirmed, int deltadeaths, int deltarecovered, int recovered, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("confirmed", confirmed);
        values.put("deaths", deaths);
        values.put("deltaconfirmed", deltaconfirmed);
        values.put("deltadeaths", deltadeaths);
        values.put("deltarecovered", deltarecovered);
        values.put("recovered", recovered);
        db.insert("TIMESERIES",null, values);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d("myapp", "onUpgrade: Hey");
    }

}
