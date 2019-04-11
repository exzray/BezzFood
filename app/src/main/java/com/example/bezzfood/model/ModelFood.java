package com.example.bezzfood.model;

import com.google.firebase.firestore.Exclude;

public class ModelFood {

    // metadata start
    private String restaurantUID = "";
    private String foodUID = "";
    private String path = "";
    // metadata end

    private String name = "";
    private String image = "";
    private String description = "";

    private Double price = 0.0;
    private Integer quantity = 0;


    @Exclude
    public String getRestaurantUID() {
        return restaurantUID;
    }

    @Exclude
    public void setRestaurantUID(String restaurantUID) {
        this.restaurantUID = restaurantUID;
    }

    @Exclude
    public String getFoodUID() {
        return foodUID;
    }

    @Exclude
    public void setFoodUID(String foodUID) {
        this.foodUID = foodUID;
    }

    @Exclude
    public String getPath() {
        return path;
    }

    @Exclude
    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
