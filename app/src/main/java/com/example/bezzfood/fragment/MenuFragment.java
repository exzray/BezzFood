package com.example.bezzfood.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bezzfood.R;
import com.example.bezzfood.adapter.FoodListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private String md_title, md_restaurantUID, md_menuUID;

    private FoodListAdapter mc_adapter;
    private GridLayoutManager mc_manager;

    private RecyclerView mv_recycler;

    private FirebaseFirestore fb_firestore = FirebaseFirestore.getInstance();


    public static MenuFragment newInstance(String title, String restaurantUID, String md_menuUID){
        MenuFragment fragment = new MenuFragment();
        fragment.md_title = title;
        fragment.md_restaurantUID = restaurantUID;
        fragment.md_menuUID = md_menuUID;

        return fragment;
    }


    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mc_adapter = new FoodListAdapter();
        mc_manager = new GridLayoutManager(getContext(), 2);

        mv_recycler = view.findViewById(R.id.recycler);

        initUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mc_adapter.removeMenu();
    }

    @Override
    @NonNull
    public String toString() {
        return md_title;
    }

    private void initUI(){
        mv_recycler.setLayoutManager(mc_manager);
        mv_recycler.setAdapter(mc_adapter);

        mc_adapter.setMenu(md_restaurantUID, md_menuUID);
    }
}
