package com.example.bezzfood.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bezzfood.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.VH> {

    private Map<String, Map> cart;

    public CartListAdapter(Map<String, Map> cart) {
        this.cart = cart;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cart, viewGroup, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        final Map data = (new ArrayList<>(cart.values())).get(i);

        if (data != null) {
            String name = (String) data.get("name");
            Double price = (Double) data.get("price");
            Long quantity = (Long) data.get("quantity");

            assert price != null;
            assert quantity != null;

            vh.setName(name);
            vh.setPrice(price);
            vh.setQuantity(quantity);

        }
    }

    @Override
    public int getItemCount() {
        return cart.size();
    }

    class VH extends RecyclerView.ViewHolder {

        private TextView name, price, quantity;
        private Button plus, minus;

        private VH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.label_name);
            price = itemView.findViewById(R.id.label_price);
            quantity = itemView.findViewById(R.id.label_quantity);

            plus = itemView.findViewById(R.id.button_plus);
            minus = itemView.findViewById(R.id.button_minus);
        }

        private void setName(String value) {
            name.setText(value);
        }

        private void setPrice(Double value) {
            price.setText(NumberFormat.getCurrencyInstance().format(value.doubleValue()));
        }

        private void setQuantity(Long value) {
            quantity.setText(String.valueOf(value.intValue()));
        }
    }
}
