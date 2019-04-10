package com.example.bezzfood.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bezzfood.R;
import com.example.bezzfood.model.ModelFood;
import com.example.bezzfood.utility.Data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.VH> {

    private Map<String, ModelFood> mc_food;
    private ListenerRegistration mc_registration;

    private FirebaseAuth fb_auth = FirebaseAuth.getInstance();
    private FirebaseFirestore fb_firestore = FirebaseFirestore.getInstance();


    public FoodListAdapter() {
        mc_food = new HashMap<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_food, viewGroup, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        final ModelFood food = (new ArrayList<>(mc_food.values())).get(i);

        vh.setImage(food.getImage());
        vh.setName(food.getName());
        vh.setPrice(food.getPrice());
        vh.setButton(food);
    }

    @Override
    public int getItemCount() {
        return mc_food.size();
    }

    public void setMenu(final String restaurantUID, final String menuUID) {
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
                            mc_food.clear();

                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                ModelFood food = snapshot.toObject(ModelFood.class);

                                if (food != null) {

                                    // save metadata
                                    food.setRestaurantUID(restaurantUID);
                                    food.setMenuUID(menuUID);
                                    food.setFoodUID(snapshot.getId());
                                    food.setPath(snapshot.getReference().getPath());

                                    mc_food.put(snapshot.getId(), food);
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
        private Button buy, remove;


        private VH(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_food);
            name = itemView.findViewById(R.id.label_name);
            price = itemView.findViewById(R.id.label_price);
            buy = itemView.findViewById(R.id.button_buy);
            remove = itemView.findViewById(R.id.button_remove);
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

        private void setButton(final ModelFood food) {

            FirebaseUser user = fb_auth.getCurrentUser();

            if (user != null){
                final DocumentReference pending = fb_firestore
                        .collection(Data.FIRESTORE_KEY_USERS)
                        .document(user.getUid())
                        .collection(Data.FIRESTORE_KEY_PENDING)
                        .document(food.getRestaurantUID());

                buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {


                        fb_firestore.runTransaction(new Transaction.Function<Void>() {
                            @android.support.annotation.Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                DocumentSnapshot snapshot = transaction.get(pending);
                                Map<String, Object> data = new HashMap<>();

                                if (!snapshot.exists()){
                                    data.put(food.getFoodUID(), 1);
                                } else {
                                    long quantity = (long) snapshot.get(food.getFoodUID());
                                    quantity++;

                                    data.put(food.getFoodUID(), quantity);
                                }

                                transaction.set(pending, data, SetOptions.merge());

                                return null;
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        fb_firestore.runTransaction(new Transaction.Function<Void>() {
                            @android.support.annotation.Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                DocumentSnapshot snapshot = transaction.get(pending);
                                Map<String, Object> data = new HashMap<>();

                                if (!snapshot.exists()){
                                    data.put(food.getFoodUID(), 1);
                                } else {
                                    long quantity = (long) snapshot.get(food.getFoodUID());
                                    quantity--;

                                    if (!(quantity <= 0)) data.put(food.getFoodUID(), quantity);
                                }


                                transaction.set(pending, data, SetOptions.merge());

                                return null;
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        }
    }
}
