package com.example.bezzfood;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.bezzfood.adapter.MenuTabAdapter;
import com.example.bezzfood.utility.Data;

public class MenuListActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_menu_list);

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
