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
import com.google.firebase.firestore.Transaction;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import jp.shts.android.library.TriangleLabelView;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.VH> {

    private Map<String, ModelFood> mc_food;
    private ListenerRegistration mc_foodRegistration;

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
        vh.setQuantity(food.getQuantity());
    }

    @Override
    public int getItemCount() {
        return mc_food.size();
    }

    public void setMenu(final String restaurantUID, final String menuUID) {
        FirebaseUser user = fb_auth.getCurrentUser();
        assert user != null;

        CollectionReference foodRef = fb_firestore
                .collection(Data.FIRESTORE_KEY_RESTAURANTS)
                .document(restaurantUID)
                .collection(Data.FIRESTORE_KEY_MENUS)
                .document(menuUID)
                .collection(Data.FIRESTORE_KEY_FOODS);

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

                    update(restaurantUID);
                }
            }
        });
    }

    private void update(String restaurantUID){
        final FirebaseUser user = fb_auth.getCurrentUser();
        assert user != null;

        final CollectionReference cart = fb_firestore
                .collection(Data.FIRESTORE_KEY_USERS)
                .document(user.getUid())
                .collection(Data.FIRESTORE_KEY_ORDERS)
                .document(restaurantUID)
                .collection(Data.FIRESTORE_KEY_CARTS);

        cart
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null){
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                                ModelFood food = mc_food.get(snapshot.getId());

                                if (food != null){
                                    Long value = snapshot.getLong("quantity");
                                    assert value != null;

                                    food.setQuantity(value.intValue());
                                }
                            }
                        }

                        notifyDataSetChanged();
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
            final FirebaseUser user = fb_auth.getCurrentUser();
            assert user != null;

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    final DocumentReference pending = fb_firestore
                            .collection(Data.FIRESTORE_KEY_USERS)
                            .document(user.getUid())
                            .collection(Data.FIRESTORE_KEY_ORDERS)
                            .document(food.getRestaurantUID())
                            .collection(Data.FIRESTORE_KEY_CARTS)
                            .document(food.getFoodUID());

                    fb_firestore.runTransaction(new Transaction.Function<Integer>() {
                        @Override
                        public Integer apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                            Map<String, Object> data;
                            Integer value = 0;

                            // retrieve the document first
                            DocumentSnapshot snapshot = transaction.get(pending);

                            // this is needed, to sure data is exist
                            if (snapshot.exists()) data = snapshot.getData();
                            else data = new HashMap<>();

                            assert data != null;

                            // update to existed value
                            if (data.containsKey("quantity")) {
                                Long lValue = (Long) data.get("quantity");

                                if (lValue != null) value = lValue.intValue();
                            }

                            // increase or decrease based on button clicked
                            if (v.equals(buy)) value++;
                            else value--;

                            // update new data
                            data.put("name", food.getName());
                            data.put("price", food.getPrice());
                            data.put("quantity", value);

                            // remove the field
                            if (value <= 0) {

                                transaction.delete(pending);

                            } else {
                                //write new data
                                transaction.set(pending, data);
                            }

                            return value;
                        }
                    })
                            .addOnSuccessListener(new OnSuccessListener<Integer>() {
                                @Override
                                public void onSuccess(Integer integer) {
                                    setQuantity(integer);
                                }
                            });
                }
            };

            buy.setOnClickListener(onClickListener);
            remove.setOnClickListener(onClickListener);
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
