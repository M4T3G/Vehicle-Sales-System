package com.carsales;

public class Admin {
    private String username;
    private String password;
    private int dealer_id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(int dealer_id) {
        this.dealer_id = dealer_id;
    }
}