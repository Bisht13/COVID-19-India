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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FragmentDataIndia extends Fragment {
    View view;
    JSONArray data;
    private LineChart mChart;
    private BarChart barChart;
    ArrayList<Entry> yValues = new ArrayList<>();
    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<String> datesGraph = new ArrayList<>();
    ArrayList<Integer> confirmedGraph = new ArrayList<>();
    ArrayList<Integer> deltaConfirmedGraph = new ArrayList<>();
    ArrayList<Integer> deathsGraph = new ArrayList<>();
    ArrayList<Integer> deltaDeathsGraph = new ArrayList<>();
    ArrayList<Integer> deltaRecoveredGraph = new ArrayList<>();
    ArrayList<Integer> recoveredGraph = new ArrayList<>();
    ArrayList<Integer> activeGraph = new ArrayList<>(confirmedGraph.size());
    int stateut = 1, confirmed = 0, active = 0, deaths = 0, recovered = 0;
    int graphStatus=1;
    int typeGraph = 1;
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
        final Button stateUTButton = view.findViewById(R.id.btnStateUT);
        final Button confirmedButton = view.findViewById(R.id.btnConfirmed);
        final Button activeButton = view.findViewById(R.id.btnActive);
        final Button deathsButton = view.findViewById(R.id.btnDeaths);
        final Button recoveredButton = view.findViewById(R.id.btnRecovered);
        final Button confirmedGraphButton = view.findViewById(R.id.confirmedButtonGraph);
        final Button activeGraphButton = view.findViewById(R.id.activeButtonGraph);
        final Button recoveredGraphButton = view.findViewById(R.id.recoveredButtonGraph);
        final Button deathsGraphButton = view.findViewById(R.id.deathsButtonGraph);
        final Button linearGraphButton = view.findViewById(R.id.linearGraphButton);
        final Button logarithmicGraphButton = view.findViewById(R.id.logarithmicGraphButton);
        barChart = view.findViewById(R.id.barchart);
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
                    confirmedtv.setText(confirmedTT+"\n"+showdelta(deltaconfirmedTT));
                    activetv.setText(activeTT+"\n"+showdelta(deltaactiveTT));
                    deathstv.setText(deathsTT+"\n"+showdelta(deltadeathsTT));
                    recoveredtv.setText(recoveredTT+"\n"+showdelta(deltarecoveredTT));
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
                        data[1].setText(confirmed+"\n"+showdelta(deltaconfirmed));
                        data[2].setText(active+"\n"+showdelta((deltaconfirmed-deltadeaths-deltarecovered)));
                        data[3].setText(deaths+"\n"+showdelta(deltadeaths));
                        data[4].setText(recovered+"\n"+showdelta(deltarecovered));
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
                        entries.add(new BarEntry(i, deltaconfirmed));
                        datesGraph.add(date);
                        confirmedGraph.add(confirmed);
                        deltaConfirmedGraph.add(deltaconfirmed);
                        deathsGraph.add(deaths);
                        deltaDeathsGraph.add(deltadeaths);
                        deltaRecoveredGraph.add(deltarecovered);
                        recoveredGraph.add(recovered);
                        activeGraph.add(confirmed-recovered-deaths);
                        i++;
                    }while(cursor.moveToNext());

                    LineDataSet set1 = new LineDataSet(yValues, "Confirmed");
                    set1.setFillAlpha(110);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);

                    LineData data = new LineData(dataSets);
                    mChart.getAxisLeft().setEnabled(false);
                    mChart.setData(data);
                    XAxis xAxis = mChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(datesGraph));
                    mChart.getDescription().setEnabled(false);
                    mChart.invalidate();

                    BarDataSet set = new BarDataSet(entries, "Confirmed");
                    BarData bardata = new BarData(set);
                    bardata.setBarWidth(0.9f); // set custom bar width
                    barChart.setData(bardata);
                    barChart.setFitBars(true); // make the x-axis fit exactly all bars
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(datesGraph));
                    barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChart.getDescription().setEnabled(false);
                    barChart.invalidate(); // refresh
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
                graphStatus = 1;
                if (typeGraph == 2) {
                    logScale();
                    logScale();
                } else {
                    yValues.clear();
                    for (int i = 0; i < confirmedGraph.size(); i++) {
                        yValues.add(new Entry(i, confirmedGraph.get(i)));
                    }
                    LineDataSet set1 = new LineDataSet(yValues, "Confirmed");
                    set1.setFillAlpha(110);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);

                    LineData data = new LineData(dataSets);
                    mChart.getAxisLeft().setEnabled(false);
                    mChart.setData(data);
                    mChart.invalidate();
                }
                entries.clear();
                for (int i = 0; i < confirmedGraph.size(); i++) {
                    entries.add(new BarEntry(i, deltaConfirmedGraph.get(i)));
                }
                BarDataSet set = new BarDataSet(entries, "Confirmed");
                BarData bardata = new BarData(set);
                bardata.setBarWidth(0.9f); // set custom bar width
                barChart.setData(bardata);
                barChart.setFitBars(true); // make the x-axis fit exactly all bars
                barChart.invalidate(); // refresh
            }
        });

        activeGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphStatus = 2;
                if (typeGraph == 2) {
                    logScale();
                    logScale();
                } else {
                    yValues.clear();
                    for (int i = 0; i < confirmedGraph.size(); i++) {
                        yValues.add(new Entry(i, confirmedGraph.get(i) - recoveredGraph.get(i) - deathsGraph.get(i)));
                    }
                    LineDataSet set1 = new LineDataSet(yValues, "Active");
                    set1.setFillAlpha(110);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);

                    LineData data = new LineData(dataSets);
                    mChart.getAxisLeft().setEnabled(false);
                    mChart.setData(data);
                    mChart.invalidate();
                }
                entries.clear();
                for (int i = 0; i < confirmedGraph.size(); i++) {
                    entries.add(new BarEntry(i, deltaConfirmedGraph.get(i) - deltaRecoveredGraph.get(i) - deltaDeathsGraph.get(i)));
                }
                BarDataSet set = new BarDataSet(entries, "Active");
                BarData bardata = new BarData(set);
                bardata.setBarWidth(0.9f); // set custom bar width
                barChart.setData(bardata);
                barChart.setFitBars(true); // make the x-axis fit exactly all bars
                barChart.invalidate(); // refresh
            }
        });

        recoveredGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphStatus = 3;
                if (typeGraph == 2) {
                    logScale();
                    logScale();
                } else {
                    yValues.clear();
                    for (int i = 0; i < recoveredGraph.size(); i++) {
                        yValues.add(new Entry(i, recoveredGraph.get(i)));
                    }
                    LineDataSet set1 = new LineDataSet(yValues, "Recovered");
                    set1.setFillAlpha(110);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);

                    LineData data = new LineData(dataSets);
                    mChart.getAxisLeft().setEnabled(false);
                    mChart.setData(data);
                    mChart.invalidate();
                }
                entries.clear();
                for (int i = 0; i < recoveredGraph.size(); i++) {
                    entries.add(new BarEntry(i, deltaRecoveredGraph.get(i)));
                }
                BarDataSet set = new BarDataSet(entries, "Recovered");
                BarData bardata = new BarData(set);
                bardata.setBarWidth(0.9f); // set custom bar width
                barChart.setData(bardata);
                barChart.setFitBars(true); // make the x-axis fit exactly all bars
                barChart.invalidate(); // refresh
            }
        });

        deathsGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphStatus = 4;
                if (typeGraph == 2){
                    logScale();
                    logScale();
                }
                else {
                    yValues.clear();
                    for (int i = 0; i < deathsGraph.size(); i++) {
                        yValues.add(new Entry(i, deathsGraph.get(i)));
                    }
                    LineDataSet set1 = new LineDataSet(yValues, "Deaths");
                    set1.setFillAlpha(110);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);

                    LineData data = new LineData(dataSets);
                    mChart.getAxisLeft().setEnabled(false);
                    mChart.setData(data);
                    mChart.invalidate();
                }
                entries.clear();
                for (int i = 0; i < deathsGraph.size(); i++) {
                    entries.add(new BarEntry(i, deltaDeathsGraph.get(i)));
                }
                BarDataSet set = new BarDataSet(entries, "Deaths");
                BarData bardata = new BarData(set);
                bardata.setBarWidth(0.9f); // set custom bar width
                barChart.setData(bardata);
                barChart.setFitBars(true); // make the x-axis fit exactly all bars
                barChart.invalidate(); // refresh
            }
        });

        stateUTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase("mydb");
                DatabaseSQLite databaseSQLite = new DatabaseSQLite(getActivity().getApplicationContext());
                databaseSQLite.data = data;
                SQLiteDatabase database = databaseSQLite.getWritableDatabase();
                if (stateut%2==1){
                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned') ORDER BY state", new String[]{});
                    createTable(cursor);
                }else if (stateut%2==0){
                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned') ORDER BY state DESC", new String[]{});
                    createTable(cursor);
                }
                stateut++;
            }
        });

        confirmedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase("mydb");
                DatabaseSQLite databaseSQLite = new DatabaseSQLite(getActivity().getApplicationContext());
                databaseSQLite.data = data;
                SQLiteDatabase database = databaseSQLite.getWritableDatabase();
                if (confirmed%2==1){
                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned') ORDER BY confirmed", new String[]{});
                    createTable(cursor);
                }else if (confirmed%2==0){
                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned') ORDER BY confirmed DESC", new String[]{});
                    createTable(cursor);
                }
                confirmed++;
            }
        });

        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase("mydb");
                DatabaseSQLite databaseSQLite = new DatabaseSQLite(getActivity().getApplicationContext());
                databaseSQLite.data = data;
                SQLiteDatabase database = databaseSQLite.getWritableDatabase();
                if(active%2==1){
                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned') ORDER BY active", new String[]{});
                    createTable(cursor);
                }else if(active%2==0){
                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned') ORDER BY active DESC", new String[]{});
                    createTable(cursor);
                }
                active++;
            }
        });

        deathsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase("mydb");
                DatabaseSQLite databaseSQLite = new DatabaseSQLite(getActivity().getApplicationContext());
                databaseSQLite.data = data;
                SQLiteDatabase database = databaseSQLite.getWritableDatabase();
                if(deaths%2==1){
                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned') ORDER BY deaths", new String[]{});
                    createTable(cursor);
                }else if(deaths%2==0){
                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned') ORDER BY deaths DESC", new String[]{});
                    createTable(cursor);
                }
                deaths++;
            }
        });

        recoveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase("mydb");
                DatabaseSQLite databaseSQLite = new DatabaseSQLite(getActivity().getApplicationContext());
                databaseSQLite.data = data;
                SQLiteDatabase database = databaseSQLite.getWritableDatabase();
                if(recovered%2==1){
                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned') ORDER BY recovered", new String[]{});
                    createTable(cursor);
                }else if(recovered%2==0){
                    Cursor cursor = database.rawQuery("SELECT state, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAINDIA WHERE state NOT IN ('Total','State Unassigned') ORDER BY recovered DESC", new String[]{});
                    createTable(cursor);
                }
                recovered++;
            }
        });

        logarithmicGraphButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                logScale();
                logScale();
            }
        });

        linearGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearScale();
            }
        });
        return view;
    }
    private float scaleCbr(double cbr) {
        return (float)(Math.log10(cbr));
    }

    private float unScaleCbr(double cbr) {
        double calcVal = Math.pow(10, cbr);
        return (float)(calcVal);
    }

    private void logScale(){
        typeGraph = 2;
        ArrayList<Integer> logdata = new ArrayList<>(confirmedGraph.size());
        String label;
        switch (graphStatus){
            case 1:
                logdata = confirmedGraph;
                label = "Confirmed";
                break;
            case 2:
                logdata = activeGraph;
                label = "Active";
                break;
            case 3:
                logdata = recoveredGraph;
                label = "Recovered";
                break;
            case 4:
                logdata = deathsGraph;
                label = "Deaths";
                break;
            default:
                label = "Confirmed";
        }
        yValues.clear();
        for (int i=0;i<logdata.size();i++){
            yValues.add(new Entry(i,scaleCbr(logdata.get(i))));
        }
        mChart.getAxisRight().setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat mFormat;
                mFormat = new DecimalFormat("##.###");
                return mFormat.format((int)unScaleCbr(value));
            }
        });
        for (int i=0;i<logdata.size();i++){
            if(logdata.get(i)==0)
                logdata.set(i, 1);
        }
        int maxScaleCbr = (int) (Math.ceil((float)logdata.get(logdata.size()-1)/Math.pow(10,String.valueOf(logdata.get(logdata.size()-1)).length()))*Math.pow(10,String.valueOf(logdata.get(logdata.size()-1)).length()));
        mChart.getAxisRight().setAxisMinimum(scaleCbr(1));
        mChart.getAxisRight().setAxisMaximum((int)scaleCbr(maxScaleCbr));
        //mChart.getAxisRight().setLabelCount(8, true);
        LineDataSet set1 = new LineDataSet(yValues, label);
        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        mChart.setData(data);
        mChart.getAxisLeft().setEnabled(false);
        //mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void linearScale(){
        typeGraph = 1;
        ArrayList<Integer> lineardata = new ArrayList<>(confirmedGraph.size());
        String label;
        switch (graphStatus){
            case 1:
                lineardata = confirmedGraph;
                label = "Confirmed";
                break;
            case 2:
                lineardata = activeGraph;
                label = "Active";
                break;
            case 3:
                lineardata = recoveredGraph;
                label = "Recovered";
                break;
            case 4:
                lineardata = deathsGraph;
                label = "Deaths";
                break;
            default:
                label = "Confirmed";
        }

        yValues.clear();
        for (int i = 0; i < lineardata.size(); i++) {
            yValues.add(new Entry(i, lineardata.get(i)));
        }
        LineDataSet set1 = new LineDataSet(yValues, label);
        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);
        mChart.setData(data);
        mChart.invalidate();

    }

    private void createTable(Cursor cursor){
        TableLayout tablestates = view.findViewById(R.id.tablestates);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        cleanTable(tablestates);
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
            data[1].setText(confirmed+"\n"+showdelta(deltaconfirmed));
            data[2].setText(active+"\n"+showdelta((deltaconfirmed-deltadeaths-deltarecovered)));
            data[3].setText(deaths+"\n"+showdelta(deltadeaths));
            data[4].setText(recovered+"\n"+showdelta(deltarecovered));
            /* Add Button to row. */
            for(int i=0;i<5;i++){
                tr.addView(data[i]);
            }

            tr.setPadding(5,5,5,5);
            /* Add row to TableLayout. */
            //tr.setBackgroundResource(R.drawable.sf_gradient_03);
            tablestates.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


        }while(cursor.moveToNext());
    }

    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }

    private String showdelta(int data){
        String returndata;
        returndata = data >= 0 ? "+"+String.valueOf(data):String.valueOf(data);
        return returndata;
    }
}
