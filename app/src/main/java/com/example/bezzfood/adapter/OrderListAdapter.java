package com.example.bezzfood.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bezzfood.R;
import com.example.bezzfood.model.ModelOrder;
import com.example.bezzfood.model.ModelRestaurant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.VH> {

    private final static List<DocumentSnapshot> list = new ArrayList<>();

    private FirebaseFirestore fb_firestore = FirebaseFirestore.getInstance();

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order, viewGroup, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        DocumentSnapshot snapshot = list.get(i);
        ModelOrder order = snapshot.toObject(ModelOrder.class);

        if (order != null) {
            vh.setName(order.getRestaurant());
            vh.setDate(order.getDate_order());
            vh.setStatus(order.getStatus());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<DocumentSnapshot> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    class VH extends RecyclerView.ViewHolder {

        private TextView name, date, status;

        private VH(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
        }

        private void setName(String uid) {
            fb_firestore
                    .collection("restaurants")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ModelRestaurant restaurant = documentSnapshot.toObject(ModelRestaurant.class);

                            if (restaurant != null) name.setText(restaurant.getName());
                        }
                    });
        }

        private void setDate(Date date) {
            String str_date = "Date: " + SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(date);

            this.date.setText(str_date);
        }

        private void setStatus(Integer status) {

            String str = "Status: ";

            switch (status){
                case 0:
                    str += "Pending";
                    break;
                case 1:
                    str += "On the way";
                    break;
                case 2:
                    str += "Arrived";
                    break;
                case 3:
                    str += "Completed";
                    break;
            }

            this.status.setText(str);
        }
    }
}
