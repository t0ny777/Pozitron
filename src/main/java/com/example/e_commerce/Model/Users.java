package com.example.e_commerce.Model;

import android.net.Uri;
import android.widget.ImageView;

public class Users {

    private String email, phone, password, image, address, description, name, uid;

    public Users()
    {

    }


    public Users(String email, String phone, String password, String image, String address, String description, String name, String uid) {
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
        this.description = description;
        this.name = name;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}

