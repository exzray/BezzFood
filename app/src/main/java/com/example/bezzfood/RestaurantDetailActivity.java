package com.example.bezzfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.bezzfood.adapter.MenuTabAdapter;
import com.example.bezzfood.utility.Data;

public class RestaurantDetailActivity extends AppCompatActivity {

    private String md_uid;
    private String md_name;

    private MenuTabAdapter mc_adapter;

    private Toolbar mv_toolbar;
    private TabLayout mv_tab;
    private ViewPager mv_pager;
    private TextView mv_sorry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        md_uid = getIntent().getStringExtra(Data.EXTRA_STRING_UID);
        md_name = getIntent().getStringExtra(Data.EXTRA_STRING_NAME);

        mc_adapter = new MenuTabAdapter(getSupportFragmentManager());

        mv_toolbar = findViewById(R.id.toolbar);
        mv_tab = findViewById(R.id.tab);
        mv_pager = findViewById(R.id.pager);
        mv_sorry = findViewById(R.id.label_sorry);

        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mc_adapter != null) mc_adapter.removeRestaurant();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.restaurant_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_cart:
                Intent intent = new Intent(this, OrderDetailActivity.class);
                intent.putExtra(Data.FIRESTORE_KEY_RESTAURANTS, md_uid);

                startActivity(intent);
        }

        return true;
    }

    private void initUI() {
        setSupportActionBar(mv_toolbar);
        setTitle(md_name);

        mv_tab.setupWithViewPager(mv_pager);
        mv_pager.setAdapter(mc_adapter);

        mc_adapter.setRestaurant(md_uid, mv_sorry);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
