package com.adityabisht.covid_19india;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Collapsible Toolbar
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);*/


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

}