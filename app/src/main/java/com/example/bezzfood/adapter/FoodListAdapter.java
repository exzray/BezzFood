package com.example.bezzfood.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bezzfood.R;
import com.example.bezzfood.model.ModelFood;
import com.example.bezzfood.utility.Data;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.VH> {

    private List<ModelFood> mc_foods;
    private ListenerRegistration mc_registration;

    private FirebaseFirestore fb_firestore = FirebaseFirestore.getInstance();


    public FoodListAdapter() {
        mc_foods = new ArrayList<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_food, viewGroup, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        final ModelFood food = mc_foods.get(i);

        vh.setImage(food.getImage());
        vh.setName(food.getName());
        vh.setPrice(food.getPrice());
    }

    @Override
    public int getItemCount() {
        return mc_foods.size();
    }

    public void setMenu(String restaurantUID, String menuUID) {
        mc_registration = fb_firestore
                .collection(Data.FIRESTORE_KEY_RESTAURANTS)
                .document(restaurantUID)
                .collection(Data.FIRESTORE_KEY_MENUS)
                .document(menuUID)
                .collection(Data.FIRESTORE_KEY_FOODS)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            mc_foods.clear();

                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                ModelFood food = snapshot.toObject(ModelFood.class);

                                if (food != null) {
                                    food.setUid(snapshot.getId());
                                    mc_foods.add(food);
                                }
                            }

                            notifyDataSetChanged();
                        }
                    }
                });
    }

    public void removeMenu() {
        if (mc_registration != null) mc_registration.remove();
    }


    class VH extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name, price;
        private Button buy;


        private VH(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_food);
            name = itemView.findViewById(R.id.label_name);
            price = itemView.findViewById(R.id.label_price);
            buy = itemView.findViewById(R.id.button_buy);
        }

        private void setImage(String url) {
            Glide
                    .with(itemView)
                    .load(url)
                    .into(this.image);
        }

        private void setName(String name) {
            this.name.setText(name);
        }

        private void setPrice(Double price) {
            this.price.setText(NumberFormat.getCurrencyInstance().format(price));
        }
    }
}
