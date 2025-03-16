package com.example.e_commerce.Model;

public class Chats {

    private String SenderName, ProductName, Date, Time, SenderImage, from_image, from_name;

    private Chats() {

    }

    public Chats(String senderName, String productName, String date, String senderImage, String time, String from_image, String from_name) {
        SenderName = senderName;
        ProductName = productName;
        Date = date;
        SenderImage = senderImage;
        Time = time;
        from_image = from_image;
        from_name = from_name;

    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getSenderImage() {
        return SenderImage;
    }

    public void setSenderImage(String senderImage) {
        SenderImage = senderImage;

    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

        public String getFrom_name() {
            return from_name;
        }

        public void setFrom_name(String from_name) {
            from_name = from_name;


    }
}
