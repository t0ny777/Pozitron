package com.example.e_commerce.Model;

public class Messages {

    public String date, time, type, message, from, to, p_id, message_id, from_image, from_name, pname;
    public boolean is_seen;

    public Messages() {

    }

    public Messages(String date, String time, String type, String message, String from, String to, String p_id, String message_id, String from_image, String from_name, String pname, Boolean is_seen) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.message = message;
        this.from = from;
        this.to = to;
        this.p_id = p_id;
        this.message_id = message_id;
        this.from_image = from_image;
        this.from_name = from_name;
        this.pname = pname;
        this.is_seen = is_seen;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getFrom_image() {
        return from_image;
    }

    public void setFrom_image(String from_image) {
        this.from_image = from_image;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public boolean isIs_seen() {
        return is_seen;
    }

    public void setIs_seen(boolean is_seen) {
        this.is_seen = is_seen;
    }
}
