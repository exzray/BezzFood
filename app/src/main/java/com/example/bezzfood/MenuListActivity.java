package com.example.bezzfood;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.bezzfood.utility.Data;

public class MenuListActivity extends AppCompatActivity {

    private String md_uid;
    private String md_name;

    private Toolbar mv_toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        md_uid = getIntent().getStringExtra(Data.EXTRA_STRING_UID);
        md_name = getIntent().getStringExtra(Data.EXTRA_STRING_NAME);

        mv_toolbar = findViewById(R.id.toolbar);

        initUI();
    }

    private void initUI(){
        setSupportActionBar(mv_toolbar);
        setTitle(md_name);

        Toast.makeText(this, "Uid: " + md_uid, Toast.LENGTH_SHORT).show();
    }
}
