package com.adityabisht.covid_19india;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {

    public static TextView test;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private Button news;
    private Button precautions;
    private Button tester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        news = findViewById(R.id.news);
        precautions = findViewById(R.id.precautions);
        tester = findViewById(R.id.tester);

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewsActivity();
            }
        });

        precautions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPrecautionsActivity();
            }
        });

        tester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTesterActivity();
            }
        });

        //Collapsible Toolbar
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        //TabLayout
         tabLayout = findViewById(R.id.tabs);
         appBarLayout = findViewById(R.id.appBarLayout);
         viewPager = findViewById(R.id.viewPager);
         ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
         //Adding Fragments in Tablayout
         adapter.addFragment(new FragmentDataIndia(), "India");
         adapter.addFragment(new FragmentDataWorld(), "World");
         viewPager.setAdapter(adapter);
         tabLayout.setupWithViewPager(viewPager);

    }

    public void startNewsActivity(){
        Intent intent = new Intent(this, NewsActivity.class);
        startActivity(intent);
    }
    public void startPrecautionsActivity(){
        Intent intent = new Intent(this, PrecautionsActivity.class);
        startActivity(intent);
    }
    public void startTesterActivity(){
        Intent intent = new Intent(this, TesterActivity.class);
        startActivity(intent);
    }
}