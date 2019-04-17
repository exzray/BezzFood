package com.example.bezzfood;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.bezzfood.utility.Data;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        String uid = getIntent().getStringExtra(Data.FIRESTORE_KEY_RESTAURANTS);

        Toast.makeText(this, "uid:"+uid, Toast.LENGTH_SHORT).show();
    }
}
