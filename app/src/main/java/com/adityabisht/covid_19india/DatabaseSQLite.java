package com.adityabisht.covid_19india;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

public class DatabaseSQLite extends SQLiteOpenHelper {
    private static final String dbName = "mydb";
    private static final int ver = 1;
    JSONArray data;

    public DatabaseSQLite(Context context) {
        super(context, dbName, null, ver);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

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

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
