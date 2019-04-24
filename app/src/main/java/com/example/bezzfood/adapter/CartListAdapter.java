package com.example.bezzfood.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bezzfood.R;
import com.example.bezzfood.model.ModelItem;
import com.example.bezzfood.utility.Data;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.VH> {

    private Map<String, ModelItem> cart;
    private String md_uid;

    private FirebaseAuth fb_auth = FirebaseAuth.getInstance();
    private FirebaseFirestore fb_firestore = FirebaseFirestore.getInstance();

    public CartListAdapter(Map<String, ModelItem> cart, String uid) {
        this.cart = cart;
        this.md_uid = uid;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cart, viewGroup, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        final ModelItem data = (new ArrayList<>(cart.values())).get(i);

        if (data != null) {
            vh.setName(data.getName());
            vh.setPrice(data.getPrice());
            vh.setQuantity(data.getQuantity());
            vh.setButton(md_uid, data.getUid());
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

        private void setButton(String r, String f){
            FirebaseUser user = fb_auth.getCurrentUser();

            assert user != null;

            final DocumentReference itemRef = fb_firestore
                    .collection(Data.FIRESTORE_KEY_USERS)
                    .document(user.getUid())
                    .collection(Data.FIRESTORE_KEY_ORDERS)
                    .document(r)
                    .collection(Data.FIRESTORE_KEY_CARTS)
                    .document(f);

            Log.i("mymsg", "setButton: " + itemRef);

            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    fb_firestore.runTransaction(new Transaction.Function<ModelItem>() {
                        @Nullable
                        @Override
                        public ModelItem apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            ModelItem item = transaction.get(itemRef).toObject(ModelItem.class);

                            if (item != null){

                                if (v.equals(plus)){

                                    item.setQuantity(item.getQuantity() + 1);

                                } else {

                                    item.setQuantity(item.getQuantity() - 1);

                                }

                                // remove the field
                                if (item.getQuantity() <= 0) {

                                    transaction.delete(itemRef);

                                } else {
                                    //write new data
                                    transaction.set(itemRef, item);
                                }
                            }

                            return item;
                        }
                    });
                }
            };

            plus.setOnClickListener(onClick);
            minus.setOnClickListener(onClick);
        }
    }
}
