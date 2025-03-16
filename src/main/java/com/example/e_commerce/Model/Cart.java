package com.example.e_commerce.Model;

public class Cart {

    private String uid, pname, price, address, phone_number, image;

    public Cart() {
    }

    public Cart(String uid, String pname, String price, String address, String phone_number, String image) {
        this.uid = uid;
        this.pname = pname;
        this.price = price;
        this.address = address;
        this.phone_number = phone_number;
        this.image = image;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

}
