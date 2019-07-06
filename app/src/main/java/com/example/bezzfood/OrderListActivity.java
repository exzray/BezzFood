package com.example.bezzfood;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.bezzfood.adapter.OrderListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class OrderListActivity extends AppCompatActivity {

    private OrderListAdapter mc_adapter;
    private LinearLayoutManager mc_manager;
    private DividerItemDecoration mc_divider;

    private RecyclerView mv_recycler;

    private FirebaseAuth fb_auth;
    private FirebaseFirestore fb_firestore;
    private ListenerRegistration fb_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        mc_adapter = new OrderListAdapter();
        mc_manager = new LinearLayoutManager(this);
        mc_divider = new DividerItemDecoration(this, mc_manager.getOrientation());

        mv_recycler = findViewById(R.id.recycler);

        fb_auth= FirebaseAuth.getInstance();
        fb_firestore = FirebaseFirestore.getInstance();

        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (fb_listener != null) fb_listener.remove();
    }

    private void initUI(){
        mv_recycler.setAdapter(mc_adapter);
        mv_recycler.setLayoutManager(mc_manager);
        mv_recycler.addItemDecoration(mc_divider);

        assert fb_auth.getCurrentUser() != null;

        fb_listener = fb_firestore
                .collection("orders")
                .whereEqualTo("user", fb_auth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) mc_adapter.setData(queryDocumentSnapshots.getDocuments());
                        Toast.makeText(OrderListActivity.this, "change", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
