package com.example.bezzfood.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bezzfood.R;
import com.example.bezzfood.model.ModelRestaurant;

import java.util.ArrayList;
import java.util.List;

public class NearbyRestaurantAdapter extends RecyclerView.Adapter<NearbyRestaurantAdapter.VH> {

    public static final List<ModelRestaurant> LIST = new ArrayList<>();

    // my component
    private OnItemClickListener mc_listener;

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_restaurant, viewGroup, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        final ModelRestaurant restaurant = LIST.get(i);

        vh.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mc_listener != null)
                    mc_listener.onItemClick(restaurant);
            }
        });

        vh.mv_name.setText(restaurant.getName());
        vh.mv_rating.setRating(restaurant.getRating().floatValue());

        Glide.with(vh.itemView).load(restaurant.getImage()).into(vh.mv_image);
    }

    @Override
    public int getItemCount() {
        return LIST.size();
    }

    class VH extends RecyclerView.ViewHolder {

        private TextView mv_name;
        private ImageView mv_image;
        private RatingBar mv_rating;


        private VH(@NonNull View itemView) {
            super(itemView);

            mv_name = itemView.findViewById(R.id.item_name);
            mv_image = itemView.findViewById(R.id.item_image);
            mv_rating = itemView.findViewById(R.id.item_rating);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(ModelRestaurant restaurant);
    }

    public void setOnItemListener(OnItemClickListener listener) {
        this.mc_listener = listener;
    }
}
