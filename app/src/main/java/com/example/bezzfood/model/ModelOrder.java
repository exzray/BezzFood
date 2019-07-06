package com.example.bezzfood.model;

import java.util.Date;
import java.util.Map;

public class ModelOrder {

    private String user = "";
    private String rider = "";
    private String restaurant = "";

    private Integer status = 1;

    private Date date_order = null;

    private Map<String, Integer> item = null;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRider() {
        return rider;
    }

    public void setRider(String rider) {
        this.rider = rider;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getDate_order() {
        return date_order;
    }

    public void setDate_order(Date date_order) {
        this.date_order = date_order;
    }

    public Map<String, Integer> getItem() {
        return item;
    }

    public void setItem(Map<String, Integer> item) {
        this.item = item;
    }
}
