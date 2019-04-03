package com.example.bezzfood.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.example.bezzfood.fragment.MenuFragment;
import com.example.bezzfood.utility.Data;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MenuTabAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mc_fragments;
    private ListenerRegistration mc_registration;

    private FirebaseFirestore fb_firestore = FirebaseFirestore.getInstance();


    public MenuTabAdapter(FragmentManager fm) {
        super(fm);

        mc_fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {
        return mc_fragments.get(i);
    }

    @Override
    public int getCount() {
        return mc_fragments.size();
    }

    @android.support.annotation.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mc_fragments.get(position).toString();
    }

    public void setRestaurant(final String restaurantUID, final View view) {
        mc_registration = fb_firestore
                .collection(Data.FIRESTORE_KEY_RESTAURANTS)
                .document(restaurantUID)
                .collection(Data.FIRESTORE_KEY_MENUS)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {

                            if (queryDocumentSnapshots.isEmpty()) view.setVisibility(View.VISIBLE);
                            else view.setVisibility(View.INVISIBLE);

                            mc_fragments.clear();

                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                mc_fragments
                                        .add(MenuFragment
                                                .newInstance(
                                                        snapshot.getString("name"), // menu title tab
                                                        restaurantUID,
                                                        snapshot.getId() // menu uid
                                                )
                                        );
                            }

                            notifyDataSetChanged();
                        }
                    }
                });
    }

    public void removeRestaurant() {
        if (mc_registration != null) mc_registration.remove();
    }
}
