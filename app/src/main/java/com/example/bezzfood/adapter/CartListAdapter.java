package com.example.bezzfood.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.VH> {

    private Map<String, Map> cart;


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        final Map data = (new ArrayList<>(cart.values())).get(i);

        String name = (String) data.get("name");
        Double price = (Double) data.get("price");
        Long quantity = (Long) data.get("quantity");
    }

    @Override
    public int getItemCount() {
        return cart.size();
    }

    class VH extends RecyclerView.ViewHolder {
        public VH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
