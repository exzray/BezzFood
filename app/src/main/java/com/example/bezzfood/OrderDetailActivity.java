package com.example.bezzfood;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.bezzfood.adapter.CartListAdapter;
import com.example.bezzfood.model.ModelItem;
import com.example.bezzfood.utility.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class OrderDetailActivity extends AppCompatActivity implements ProSwipeButton.OnSwipeListener {

    private String md_uid;
    private Double md_total;
    private Map<String, ModelItem> cart;

    private LinearLayoutManager mc_manager;
    private DividerItemDecoration mc_divider;
    private CartListAdapter mc_adapter;
    private ListenerRegistration mc_listener;

    private RecyclerView mv_recycler;
    private ProSwipeButton mv_finalize;

    private FirebaseAuth fb_auth = FirebaseAuth.getInstance();
    private FirebaseFirestore fb_firestore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        md_uid = getIntent().getStringExtra(Data.FIRESTORE_KEY_RESTAURANTS);

        cart = new HashMap<>();

        mc_manager = new LinearLayoutManager(this);
        mc_divider = new DividerItemDecoration(this, mc_manager.getOrientation());
        mc_adapter = new CartListAdapter(cart, md_uid);

        mv_recycler = findViewById(R.id.recycler);
        mv_finalize = findViewById(R.id.finalize);

        initUI();

        if (md_uid != null && !md_uid.isEmpty()) {
            loadCart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mc_listener != null) mc_listener.remove();
    }

    @Override
    public void onSwipeConfirm() {
        AlertDialog dialog = new AlertDialog
                .Builder(this)
                .setMessage("Are you sure to finalize all item in this order?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalizeCart();
                        dialog.dismiss();
                        mv_finalize.showResultIcon(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mv_finalize.showResultIcon(false);
                    }
                })
                .setCancelable(false)
                .create();

        dialog.show();
    }

    private void initUI() {
        mv_recycler.setLayoutManager(mc_manager);
        mv_recycler.addItemDecoration(mc_divider);
        mv_recycler.setAdapter(mc_adapter);

        mv_finalize.setOnSwipeListener(this);
    }

    private void loadCart() {
        FirebaseUser user = fb_auth.getCurrentUser();

        if (user != null) {
            CollectionReference collectionReference = fb_firestore
                    .collection(Data.FIRESTORE_KEY_USERS)
                    .document(user.getUid())
                    .collection(Data.FIRESTORE_KEY_ORDERS)
                    .document(md_uid)
                    .collection(Data.FIRESTORE_KEY_CARTS);

            mc_listener = collectionReference
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots != null) {

                                cart.clear();

                                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    ModelItem item = snapshot.toObject(ModelItem.class);

                                    assert item != null;

                                    // put extra data
                                    item.setUid(snapshot.getId());

                                    cart.put(snapshot.getId(), item);
                                }

                                mc_adapter.notifyDataSetChanged();
                                updateTotal(cart.values());
                            }
                        }
                    });
        }
    }

    private void updateTotal(Collection<ModelItem> cart){

        md_total = 0.0;

        for (ModelItem item : cart){
            int quantity = item.getQuantity().intValue();
            double price = item.getPrice();
            md_total += (quantity * price);
        }

        mv_finalize.setText("Finalize( "+ NumberFormat.getCurrencyInstance().format(md_total) +" )");
    }

    private void finalizeCart() {

    }
}
