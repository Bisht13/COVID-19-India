package com.adityabisht.covid_19india;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentDataIndia extends Fragment {
    View view;
    JSONArray data;
    private LineChart mChart;
    public FragmentDataIndia() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.data_india,container,false);
        final TextView confirmedtv = view.findViewById(R.id.confirmed);
        final TextView activetv = view.findViewById(R.id.active);
        final TextView deathstv = view.findViewById(R.id.deaths);
        final TextView recoveredtv = view.findViewById(R.id.recovered);

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://api.covid19india.org/data.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    int confirmedTT = response.getJSONArray("statewise").getJSONObject(0).getInt("confirmed");
                    int deltaconfirmedTT = response.getJSONArray("statewise").getJSONObject(0).getInt("deltaconfirmed");
                    int activeTT = response.getJSONArray("statewise").getJSONObject(0).getInt("active");
                    int deathsTT = response.getJSONArray("statewise").getJSONObject(0).getInt("deaths");
                    int deltadeathsTT = response.getJSONArray("statewise").getJSONObject(0).getInt("deltadeaths");
                    int recoveredTT = response.getJSONArray("statewise").getJSONObject(0).getInt("recovered");
                    int deltarecoveredTT = response.getJSONArray("statewise").getJSONObject(0).getInt("deltarecovered");
                    int deltaactiveTT = deltaconfirmedTT - deltadeathsTT - deltarecoveredTT;
                    confirmedtv.setText(confirmedTT+"\n"+deltaconfirmedTT);
                    activetv.setText(activeTT+"\n"+deltaactiveTT);
                    deathstv.setText(deathsTT+"\n"+deltadeathsTT);
                    recoveredtv.setText(recoveredTT+"\n"+deltarecoveredTT);
                    data = response.getJSONArray("statewise");

                    //Reading and Manipulating DB
                    getActivity().getApplicationContext().deleteDatabase("mydb");
                    DatabaseSQLite databaseSQLite = new DatabaseSQLite(getActivity().getApplicationContext());
                    databaseSQLite.data = data;
                    SQLiteDatabase database = databaseSQLite.getWritableDatabase();

                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned')", new String[]{});

                    if (cursor!=null){
                        cursor.moveToFirst();
                    }

                    do{
                        String state = cursor.getString(0);
                        int confirmed = cursor.getInt(1);
                        int deltaconfirmed = cursor.getInt(2);
                        int active = cursor.getInt(3);
                        int deaths = cursor.getInt(4);
                        int deltadeaths = cursor.getInt(5);
                        int deltarecovered = cursor.getInt(6);
                        int recovered = cursor.getInt(7);

                        //Creating rows
                        TableLayout tablestates = view.findViewById(R.id.tablestates);
                        TableRow tr = new TableRow(getActivity().getApplicationContext());
                        TextView stateutcol = new TextView(getActivity().getApplicationContext());
                        TextView confirmedcol = new TextView(getActivity().getApplicationContext());
                        TextView activecol = new TextView(getActivity().getApplicationContext());
                        TextView deathscol = new TextView(getActivity().getApplicationContext());
                        TextView recoveredcol = new TextView(getActivity().getApplicationContext());
                        TextView[] data = new TextView[5];
                        data[0] = stateutcol;
                        data[1] = confirmedcol;
                        data[2] = activecol;
                        data[3] = deathscol;
                        data[4] = recoveredcol;
                        for (int i=0;i<5;i++){
                            //data[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                            data[i].setTextColor(Color.BLUE);
                            data[i].setBackgroundColor(Color.WHITE);
                            data[i].setTextSize(13);
                            data[i].setGravity(1);
                            data[i].setWidth(TableRow.LayoutParams.WRAP_CONTENT);
                        }
                        data[0].setText(state+"\n");
                        data[1].setText(confirmed+"\n"+deltaconfirmed);
                        data[2].setText(active+"\n"+(deltaconfirmed-deltadeaths-deltarecovered));
                        data[3].setText(deaths+"\n"+deltadeaths);
                        data[4].setText(recovered+"\n"+deltarecovered);
                        /* Add Button to row. */
                        for(int i=0;i<5;i++){
                            tr.addView(data[i]);
                        }

                        tr.setPadding(5,5,5,5);
                        /* Add row to TableLayout. */
                        //tr.setBackgroundResource(R.drawable.sf_gradient_03);
                        tablestates.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


                    }while(cursor.moveToNext());


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "Something went wrong");

            }
        });

        JsonObjectRequest charts = new JsonObjectRequest(Request.Method.GET, "https://api.covid19india.org/v4/timeseries.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    getActivity().getApplicationContext().deleteDatabase("mydb");
                    DatabaseSQLite databaseSQLite = new DatabaseSQLite(getActivity().getApplicationContext());
                    databaseSQLite.datatimeseries = response.getJSONObject("TT").getJSONObject("dates");
                    SQLiteDatabase database = databaseSQLite.getWritableDatabase();

                    Cursor cursor = database.rawQuery("SELECT date, confirmed, deltaconfirmed, deaths, deltadeaths, deltarecovered, recovered FROM TIMESERIES ORDER BY date DESC", new String[]{});
                    if (cursor!=null){
                        cursor.moveToFirst();
                    }

                    do{
                        String date = cursor.getString(0);
                        int confirmed = cursor.getInt(1);
                        int deltaconfirmed = cursor.getInt(2);
                        int deaths = cursor.getInt(3);
                        int deltadeaths = cursor.getInt(4);
                        int deltarecovered = cursor.getInt(5);
                        int recovered = cursor.getInt(6);
                        Log.d("myapp", "onResponse: "+date);
                        Log.d("myapp", "onResponse: "+confirmed);
                        Log.d("myapp", "onResponse: "+deltaconfirmed);
                        Log.d("myapp", "onResponse: "+deaths);
                        Log.d("myapp", "onResponse: "+deltadeaths);
                        Log.d("myapp", "onResponse: "+deltarecovered);
                        Log.d("myapp", "onResponse: "+recovered);
                    }while(cursor.moveToNext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);
        requestQueue.add(charts);

        mChart = view.findViewById(R.id.linechart);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();
        yValues.add(new Entry(0, 60f));
        yValues.add(new Entry(1, 30f));
        yValues.add(new Entry(2, 70f));
        yValues.add(new Entry(3, 25f));
        yValues.add(new Entry(4, 60f));
        yValues.add(new Entry(5, 90f));
        LineDataSet set1 = new LineDataSet(yValues, "Data Set 1");
        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);
        mChart.setData(data);



        return view;
    }
}
