package com.adityabisht.covid_19india;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    ArrayList<Entry> yValues = new ArrayList<>();
    ArrayList<String> datesGraph = new ArrayList<>();
    ArrayList<Integer> confirmedGraph = new ArrayList<>();
    ArrayList<Integer> deltaConfirmedGraph = new ArrayList<>();
    ArrayList<Integer> deathsGraph = new ArrayList<>();
    ArrayList<Integer> deltaDeathsGraph = new ArrayList<>();
    ArrayList<Integer> deltaRecoveredGraph = new ArrayList<>();
    ArrayList<Integer> recoveredGraph = new ArrayList<>();
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
        final Button confirmedGraphButton = view.findViewById(R.id.confirmedButtonGraph);
        final Button activeGraphButton = view.findViewById(R.id.activeButtonGraph);
        final Button recoveredGraphButton = view.findViewById(R.id.recoveredButtonGraph);
        final Button deathsGraphButton = view.findViewById(R.id.deathsButtonGraph);
        final Button linearGraphButton = view.findViewById(R.id.linearGraphButton);
        final Button logarithmicGraphButton = view.findViewById(R.id.logarithmicGraphButton);
        mChart = view.findViewById(R.id.linechart);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

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

                    Cursor cursor = database.rawQuery("SELECT date, confirmed, deltaconfirmed, deaths, deltadeaths, deltarecovered, recovered FROM TIMESERIES ORDER BY date", new String[]{});
                    if (cursor!=null){
                        cursor.moveToFirst();
                    }
                    int i=0;
                    do{
                        String date = cursor.getString(0);
                        int confirmed = cursor.getInt(1);
                        int deltaconfirmed = cursor.getInt(2);
                        int deaths = cursor.getInt(3);
                        int deltadeaths = cursor.getInt(4);
                        int deltarecovered = cursor.getInt(5);
                        int recovered = cursor.getInt(6);
                        yValues.add(new Entry(i, confirmed));
                        datesGraph.add(date);
                        confirmedGraph.add(confirmed);
                        deltaConfirmedGraph.add(deltaconfirmed);
                        deathsGraph.add(deaths);
                        deltaDeathsGraph.add(deltadeaths);
                        deltaRecoveredGraph.add(deltarecovered);
                        recoveredGraph.add(recovered);
                        i++;
                    }while(cursor.moveToNext());

                    LineDataSet set1 = new LineDataSet(yValues, "Confirmed");
                    set1.setFillAlpha(110);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);

                    LineData data = new LineData(dataSets);
                    mChart.setData(data);
                    mChart.invalidate();
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

        confirmedGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yValues.clear();
                for(int i =0;i<confirmedGraph.size();i++){
                    yValues.add(new Entry(i, confirmedGraph.get(i)));
                }
                LineDataSet set1 = new LineDataSet(yValues, "Confirmed");
                set1.setFillAlpha(110);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                LineData data = new LineData(dataSets);
                mChart.setData(data);
                mChart.invalidate();
            }
        });

        activeGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yValues.clear();
                for (int i=0;i<confirmedGraph.size();i++){
                    yValues.add(new Entry(i, confirmedGraph.get(i)-recoveredGraph.get(i)-deathsGraph.get(i)));
                }
                LineDataSet set1 = new LineDataSet(yValues, "Active");
                set1.setFillAlpha(110);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                LineData data = new LineData(dataSets);
                mChart.setData(data);
                mChart.invalidate();
            }
        });

        recoveredGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yValues.clear();
                for (int i=0;i<recoveredGraph.size();i++){
                    yValues.add(new Entry(i, recoveredGraph.get(i)));
                }
                LineDataSet set1 = new LineDataSet(yValues, "Recovered");
                set1.setFillAlpha(110);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                LineData data = new LineData(dataSets);
                mChart.setData(data);
                mChart.invalidate();
            }
        });

        deathsGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yValues.clear();
                for (int i=0;i<deathsGraph.size();i++){
                    yValues.add(new Entry(i, deathsGraph.get(i)));
                }
                LineDataSet set1 = new LineDataSet(yValues, "Deaths");
                set1.setFillAlpha(110);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                LineData data = new LineData(dataSets);
                mChart.setData(data);
                mChart.invalidate();
            }
        });
        return view;
    }
}
