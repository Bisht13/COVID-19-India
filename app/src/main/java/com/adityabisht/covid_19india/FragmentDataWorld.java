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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentDataWorld extends Fragment {
    View view;
    JSONArray data;
    int stateut = 1, confirmed = 0, active = 0, deaths = 0, recovered = 0;
    public FragmentDataWorld() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.data_world,container,false);
        final TextView confirmedtv = view.findViewById(R.id.confirmed);
        final TextView activetv = view.findViewById(R.id.active);
        final TextView deathstv = view.findViewById(R.id.deaths);
        final TextView recoveredtv = view.findViewById(R.id.recovered);
        final Button stateUTButton = view.findViewById(R.id.btnStateUT);
        final Button confirmedButton = view.findViewById(R.id.btnConfirmed);
        final Button activeButton = view.findViewById(R.id.btnActive);
        final Button deathsButton = view.findViewById(R.id.btnDeaths);
        final Button recoveredButton = view.findViewById(R.id.btnRecovered);

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://api.covid19api.com/summary", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    int confirmedTT = response.getJSONObject("Global").getInt("TotalConfirmed");
                    int deltaconfirmedTT = response.getJSONObject("Global").getInt("NewConfirmed");
                    int deathsTT = response.getJSONObject("Global").getInt("TotalDeaths");
                    int deltadeathsTT = response.getJSONObject("Global").getInt("NewDeaths");
                    int recoveredTT = response.getJSONObject("Global").getInt("TotalRecovered");
                    int deltarecoveredTT = response.getJSONObject("Global").getInt("NewRecovered");
                    int activeTT = confirmedTT-deathsTT-recoveredTT;
                    int deltaactiveTT = deltaconfirmedTT - deltadeathsTT - deltarecoveredTT;
                    confirmedtv.setText(confirmedTT+"\n"+showdelta(deltaconfirmedTT));
                    activetv.setText(activeTT+"\n"+showdelta(deltaactiveTT));
                    deathstv.setText(deathsTT+"\n"+showdelta(deltadeathsTT));
                    recoveredtv.setText(recoveredTT+"\n"+showdelta(deltarecoveredTT));
                    data = response.getJSONArray("Countries");
                    //Reading and Manipulating DB
                    getActivity().getApplicationContext().deleteDatabase("db");
                    DatabaseSQLiteWorld databaseSQLiteWorld = new DatabaseSQLiteWorld(getActivity().getApplicationContext());
                    databaseSQLiteWorld.data = data;
                    SQLiteDatabase database = databaseSQLiteWorld.getWritableDatabase();
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY confirmed DESC", new String[]{});
                    if (cursor!=null){
                        cursor.moveToFirst();
                    }
                    do{
                        String country = cursor.getString(0);
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
                        data[0].setText(country+"\n");
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


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("myapp", "onResponse: "+e);
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "Something went wrong");

            }
        });
        requestQueue.add(jsonObjectRequest);

        stateUTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase("db");
                DatabaseSQLiteWorld databaseSQLiteWorld = new DatabaseSQLiteWorld(getActivity().getApplicationContext());
                databaseSQLiteWorld.data = data;
                SQLiteDatabase database = databaseSQLiteWorld.getWritableDatabase();
                if (stateut%2==1){
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY country", new String[]{});
                    createTable(cursor);
                }else if (stateut%2==0){
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY country DESC", new String[]{});
                    createTable(cursor);
                }
                stateut++;
            }
        });

        confirmedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase("db");
                DatabaseSQLiteWorld databaseSQLiteWorld = new DatabaseSQLiteWorld(getActivity().getApplicationContext());
                databaseSQLiteWorld.data = data;
                SQLiteDatabase database = databaseSQLiteWorld.getWritableDatabase();
                if (confirmed%2==1){
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY confirmed", new String[]{});
                    createTable(cursor);
                }else if (confirmed%2==0){
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY confirmed DESC", new String[]{});
                    createTable(cursor);
                }
                confirmed++;
            }
        });

        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase("db");
                DatabaseSQLiteWorld databaseSQLiteWorld = new DatabaseSQLiteWorld(getActivity().getApplicationContext());
                databaseSQLiteWorld.data = data;
                SQLiteDatabase database = databaseSQLiteWorld.getWritableDatabase();
                if(active%2==1){
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY active", new String[]{});
                    createTable(cursor);
                }else if(active%2==0){
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY active DESC", new String[]{});
                    createTable(cursor);
                }
                active++;
            }
        });

        deathsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase("db");
                DatabaseSQLiteWorld databaseSQLiteWorld = new DatabaseSQLiteWorld(getActivity().getApplicationContext());
                databaseSQLiteWorld.data = data;
                SQLiteDatabase database = databaseSQLiteWorld.getWritableDatabase();
                if(deaths%2==1){
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY deaths", new String[]{});
                    createTable(cursor);
                }else if(deaths%2==0){
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY deaths DESC", new String[]{});
                    createTable(cursor);
                }
                deaths++;
            }
        });

        recoveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase("db");
                DatabaseSQLiteWorld databaseSQLiteWorld = new DatabaseSQLiteWorld(getActivity().getApplicationContext());
                databaseSQLiteWorld.data = data;
                SQLiteDatabase database = databaseSQLiteWorld.getWritableDatabase();
                if(recovered%2==1){
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY recovered", new String[]{});
                    createTable(cursor);
                }else if(recovered%2==0){
                    Cursor cursor = database.rawQuery("SELECT country, confirmed, deltaconfirmed, active, deaths, deltadeaths, deltarecovered, recovered FROM DATAWORLD ORDER BY recovered DESC", new String[]{});
                    createTable(cursor);
                }
                recovered++;
            }
        });

        return view;
    }
    private String showdelta(int data){
        String returndata;
        returndata = data >= 0 ? "+"+String.valueOf(data):String.valueOf(data);
        return returndata;
    }

    private void createTable(Cursor cursor){
        TableLayout tablestates = view.findViewById(R.id.tablestates);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        cleanTable(tablestates);
        do{
            String country = cursor.getString(0);
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
            data[0].setText(country+"\n");
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
    }

    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }
}
