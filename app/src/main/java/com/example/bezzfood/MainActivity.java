package com.example.bezzfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bezzfood.adapter.NearbyRestaurantAdapter;
import com.example.bezzfood.model.ModelProfile;
import com.example.bezzfood.model.ModelRestaurant;
import com.example.bezzfood.utility.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import static com.example.bezzfood.utility.Data.FIRESTORE_KEY_USERS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle mc_toggle;
    private NearbyRestaurantAdapter mc_adapter;
    private LinearLayoutManager mc_manager;
    private ListenerRegistration mc_registration;

    private Toolbar mv_toolbar;
    private DrawerLayout mv_drawer;
    private NavigationView mv_navigation;
    private RecyclerView mv_recycler;

    private FirebaseAuth fb_auth;
    private FirebaseFirestore fb_firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mc_adapter = new NearbyRestaurantAdapter();
        mc_manager = new LinearLayoutManager(this);

        mv_toolbar = findViewById(R.id.toolbar);
        mv_drawer = findViewById(R.id.drawer_layout);
        mv_navigation = findViewById(R.id.nav_view);
        mv_recycler = findViewById(R.id.recycler);

        mc_toggle = new ActionBarDrawerToggle(
                this, mv_drawer, mv_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        fb_auth = FirebaseAuth.getInstance();
        fb_firestore = FirebaseFirestore.getInstance();

        initUI();
        updateHeader();
        initNearbyRestaurant();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mc_registration != null) mc_registration.remove();
    }

    @Override
    public void onBackPressed() {
        if (mv_drawer.isDrawerOpen(GravityCompat.START)) {
            mv_drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_profile:
                FirebaseUser fb_user = fb_auth.getCurrentUser();

                assert fb_user != null;

                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(Data.EXTRA_PROFILE_USER, fb_user.getUid());
                startActivity(intent);
                break;
            case R.id.nav_order:
                break;
            case R.id.nav_logout:
                break;
        }

        mv_drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void initUI() {
        setSupportActionBar(mv_toolbar);

        mv_drawer.addDrawerListener(mc_toggle);
        mc_toggle.syncState();

        mv_navigation.setNavigationItemSelectedListener(this);

        NearbyRestaurantAdapter.OnItemClickListener onItemClickListener = new NearbyRestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModelRestaurant restaurant) {
                Intent intent = new Intent(MainActivity.this, MenuListActivity.class);

                // pass data
                intent.putExtra(Data.EXTRA_STRING_UID, restaurant.getUid());
                intent.putExtra(Data.EXTRA_STRING_NAME, restaurant.getName());
                intent.putExtra(Data.EXTRA_STRING_DESCRIPTION, restaurant.getDescription());

                startActivity(intent);
            }
        };

        mc_adapter.setOnItemListener(onItemClickListener);

        mv_recycler.setAdapter(mc_adapter);
        mv_recycler.setLayoutManager(mc_manager);
    }

    private void updateHeader() {
        View header = mv_navigation.getHeaderView(0);

        final ImageView avatar = header.findViewById(R.id.image_avatar);
        final TextView name = header.findViewById(R.id.label_name);
        final TextView email = header.findViewById(R.id.label_email);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        FirebaseUser user = fb_auth.getCurrentUser();
        assert user != null;

        fb_firestore
                .collection(FIRESTORE_KEY_USERS)
                .document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        FirebaseUser fb_user = fb_auth.getCurrentUser();
                        assert fb_user != null;

                        if (task.isSuccessful()){

                            if (task.getResult() != null && task.getResult().exists()){

                                // user data
                                ModelProfile profile = task.getResult().toObject(ModelProfile.class);

                                // must not null, i sure
                                assert profile != null;

                                // assigning data
                                name.setText(profile.getName());
                                email.setText(fb_user.getEmail());

                                Glide.with(MainActivity.this).load(profile.getAvatar()).into(avatar);

                            } else {
                                // open profile activity and
                                // create profile first
                                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                intent.putExtra(Data.EXTRA_PROFILE_TITLE, "Update Profile");
                                intent.putExtra(Data.EXTRA_PROFILE_MESSAGE, "Please update profile for the first time!");
                                intent.putExtra(Data.EXTRA_PROFILE_USER, fb_user.getUid());

                                startActivity(intent);
                                finish();
                            }

                        } else {
                            // remove annoying error message
                            assert task.getException() != null;
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }
                });
    }

    private void initNearbyRestaurant() {
        mc_registration = fb_firestore
                .collection("restaurants")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        // clear list when receive a update
                        NearbyRestaurantAdapter.LIST.clear();

                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                ModelRestaurant restaurant = snapshot.toObject(ModelRestaurant.class);

                                if (restaurant != null) {
                                    // save object uid
                                    restaurant.setUid(snapshot.getId());

                                    NearbyRestaurantAdapter.LIST.add(restaurant);
                                }
                            }
                        }

                        // notify adapter after update receive
                        mc_adapter.notifyDataSetChanged();
                    }
                });
    }
}
