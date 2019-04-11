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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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

import jp.shts.android.library.TriangleLabelView;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.VH> {

    private Map<String, ModelFood> mc_food;
    private FirebaseUser mc_user;
    private ListenerRegistration mc_foodRegistration;

    private FirebaseAuth fb_auth = FirebaseAuth.getInstance();
    private FirebaseFirestore fb_firestore = FirebaseFirestore.getInstance();


    public FoodListAdapter() {
        mc_food = new HashMap<>();
        mc_user = fb_auth.getCurrentUser();
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
        vh.setQuantity(food.getQuantity());
    }

    @Override
    public int getItemCount() {
        return mc_food.size();
    }

    public void setMenu(final String restaurantUID, final String menuUID) {
        assert mc_user != null;

        CollectionReference foodRef = fb_firestore
                .collection(Data.FIRESTORE_KEY_RESTAURANTS)
                .document(restaurantUID)
                .collection(Data.FIRESTORE_KEY_MENUS)
                .document(menuUID)
                .collection(Data.FIRESTORE_KEY_FOODS);

        final DocumentReference cartRef = fb_firestore
                .collection(Data.FIRESTORE_KEY_USERS)
                .document(mc_user.getUid())
                .collection(Data.FIRESTORE_KEY_PENDING)
                .document(restaurantUID);

        mc_foodRegistration = foodRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    mc_food.clear();

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        ModelFood food = snapshot.toObject(ModelFood.class);

                        if (food != null) {

                            // save metadata
                            food.setRestaurantUID(restaurantUID);
                            food.setFoodUID(snapshot.getId());
                            food.setPath(snapshot.getReference().getPath());

                            mc_food.put(snapshot.getId(), food);
                        }
                    }

                    cartRef
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()){
                                        Map<String, Object> data = documentSnapshot.getData();

                                        if (data != null){
                                            for (String key : data.keySet()){
                                                Long total = (Long) data.get(key);
                                                ModelFood food = mc_food.get(key);

                                                assert total != null;

                                                if (food != null) {
                                                    food.setQuantity(total.intValue());
                                                }

                                                notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("mymsg", "onFailure: " + e.getMessage());
                                }
                            });
                }
            }
        });
    }

    public void removeMenu() {
        if (mc_foodRegistration != null) mc_foodRegistration.remove();
    }


    class VH extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name, price;
        private Button buy, remove;
        private TriangleLabelView quantity;


        private VH(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_food);
            name = itemView.findViewById(R.id.label_name);
            price = itemView.findViewById(R.id.label_price);
            buy = itemView.findViewById(R.id.button_buy);
            remove = itemView.findViewById(R.id.button_remove);
            quantity = itemView.findViewById(R.id.label_quantity);
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

            if (mc_user != null) {
                final DocumentReference pending = fb_firestore
                        .collection(Data.FIRESTORE_KEY_USERS)
                        .document(mc_user.getUid())
                        .collection(Data.FIRESTORE_KEY_PENDING)
                        .document(food.getRestaurantUID());

                buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        fb_firestore.runTransaction(new Transaction.Function<Integer>() {
                            @Override
                            public Integer apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                DocumentSnapshot snapshot = transaction.get(pending);
                                Map<String, Object> data = new HashMap<>();
                                Long total = snapshot.getLong(food.getFoodUID());

                                if (total == null) {
                                    total = 0L;
                                }

                                total += 1L;
                                data.put(food.getFoodUID(), total);

                                transaction.set(pending, data, SetOptions.merge());

                                return total.intValue();
                            }
                        })
                                .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                    @Override
                                    public void onSuccess(Integer integer) {
                                        setQuantity(integer);
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

                        fb_firestore.runTransaction(new Transaction.Function<Integer>() {
                            @Override
                            public Integer apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                DocumentSnapshot snapshot = transaction.get(pending);
                                Map<String, Object> data = new HashMap<>();
                                Long total = snapshot.getLong(food.getFoodUID());

                                if (total == null) {
                                    total = 0L;
                                }

                                if (!(total <= 0)) {
                                    total -= 1L;
                                    data.put(food.getFoodUID(), total);
                                }

                                transaction.set(pending, data, SetOptions.merge());

                                return total.intValue();
                            }
                        })
                                .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                    @Override
                                    public void onSuccess(Integer integer) {
                                        setQuantity(integer);
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

        private void setQuantity(int total) {
            if (total > 0) {

                quantity.setVisibility(View.VISIBLE);
                quantity.setSecondaryText(String.valueOf(total));

            } else {

                quantity.setVisibility(View.INVISIBLE);

            }
        }
    }
}
