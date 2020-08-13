package com.adityabisht.covid_19india;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Toast;

import com.adityabisht.covid_19india.api.ApiInterface;
import com.adityabisht.covid_19india.api.apiClient;
import com.adityabisht.covid_19india.models.Article;
import com.adityabisht.covid_19india.models.News;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {
    private Button backbutton;
    public static final String API_KEY = "1adf075e75644c16835b104240bee180";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private String TAG = NewsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(NewsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backbutton();
            }
        });
        LoadJson();
    }
    public void backbutton(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    News getNewsObject(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("articles");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Object> calledarticles = (ArrayList<Object>) snapshot.getValue();
                Log.d("myapp", "onDataChange: "+String.valueOf(calledarticles));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
/*        Gson gson = new Gson();
        News news
                = gson.fromJson(OrganisationJson,
                News.class);*/

        // return object
        return null;
    }
    public void LoadJson(){
        ApiInterface apiInterface = apiClient.getApiClient().create(ApiInterface.class);

        String country = "in";

        Call<News> call;
        call = apiInterface.getNews(country, API_KEY);
        Log.d("myapp", "LoadJson: hi");
        Log.d("myapp", "LoadJson: "+String.valueOf(call));
        Log.d("myapp", "LoadJson: bye");
        getNewsObject();

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticle() != null){

                    if (!articles.isEmpty()){
                        articles.clear();
                    }

                    FirebaseDatabase database = FirebaseDatabase.getInstance();


                    articles = response.body().getArticle();
                    Log.d("myapp", "onResponse: "+String.valueOf(response));
                    newsAdapter = new NewsAdapter(articles, NewsActivity.this);
                    recyclerView.setAdapter(newsAdapter);
                    newsAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(NewsActivity.this, "No Result!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }
        });
    }
}