package com.example.bezzfood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.bezzfood.model.ModelFood;
import com.example.bezzfood.utility.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class OrderDetailActivity extends AppCompatActivity {

    private String md_uid;
    private Map<String, ModelFood> foods;
    private Map<String, Integer> cart;

    private RecyclerView mv_recycler;
    private ProSwipeButton mv_finalize;

    private FirebaseAuth fb_auth = FirebaseAuth.getInstance();
    private FirebaseFirestore fb_firestore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        md_uid = getIntent().getStringExtra(Data.FIRESTORE_KEY_RESTAURANTS);

        foods = new HashMap<>();
        cart = new HashMap<>();

        mv_recycler = findViewById(R.id.recycler);
        mv_finalize = findViewById(R.id.finalize);
    }

    private void initUI() {

    }

    private void loadFood() {
        DocumentReference docRef = fb_firestore
                .collection(Data.FIRESTORE_KEY_RESTAURANTS)
                .document(md_uid);

    }

    private void loadCart() {
        FirebaseUser user = fb_auth.getCurrentUser();


        if (user != null) {

        }
    }
}
