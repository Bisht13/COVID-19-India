package com.adityabisht.covid_19india;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentDataIndia extends Fragment {
    View view;
    JSONArray data;
    public FragmentDataIndia() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.data_india,container,false);
        final TextView confirmed = view.findViewById(R.id.confirmed);
        final TextView active = view.findViewById(R.id.active);
        final TextView deaths = view.findViewById(R.id.deaths);
        final TextView recovered = view.findViewById(R.id.recovered);
        final TextView testdatatext = view.findViewById(R.id.testdatatext);

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://api.covid19india.org/data.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String confirmedTT = response.getJSONArray("statewise").getJSONObject(0).getString("confirmed");
                    String activeTT = response.getJSONArray("statewise").getJSONObject(0).getString("active");
                    String deathsTT = response.getJSONArray("statewise").getJSONObject(0).getString("deaths");
                    String recoveredTT = response.getJSONArray("statewise").getJSONObject(0).getString("recovered");
                    confirmed.setText(confirmedTT);
                    active.setText(activeTT);
                    deaths.setText(deathsTT);
                    recovered.setText(recoveredTT);
                    data = response.getJSONArray("statewise");

                    //Reading and Manipulating DB
                    DatabaseSQLite databaseSQLite = new DatabaseSQLite(getActivity().getApplicationContext());
                    databaseSQLite.data = data;
                    SQLiteDatabase database = databaseSQLite.getWritableDatabase();

                    Cursor cursor = database.rawQuery("SELECT state, confirmed FROM DATAINDIA", new String[]{});

                    if (cursor!=null){
                        cursor.moveToFirst();
                    }

                    StringBuilder stringBuilder = new StringBuilder();

                    do{
                        String statedata = cursor.getString(0);
                        int confirmeddata = cursor.getInt(1);
                        stringBuilder.append("State: "+statedata+" Confirmed: "+confirmeddata);
                    }while(cursor.moveToNext());

                    testdatatext.setText(stringBuilder.toString());

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

        requestQueue.add(jsonObjectRequest);

        return view;
    }
}
