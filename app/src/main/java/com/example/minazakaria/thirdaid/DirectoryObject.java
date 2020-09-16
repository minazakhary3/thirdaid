package com.example.minazakaria.thirdaid;

/**
 * Created by Mina Zakaria on 4/10/2018.
 */

public class DirectoryObject{

    String id;
    String title;
    String desc;
    String phoneNumDisplay;
    String phoneNum;
    String category;
    Double Long;
    Double Lat;


    public DirectoryObject(String id, String title, String desc, String phoneNum1, String num) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.phoneNumDisplay = phoneNum1;
        this.phoneNum = num;
        this.Lat = 31.5;
        this.Long = 30.0;
    }

    public DirectoryObject(String id, String title, String desc, String phoneNum1, String num, String category) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.phoneNumDisplay = phoneNum1;
        this.phoneNum = num;
        this.category = category;
    }

    public String getPhoneNumDisplay() {
        return phoneNumDisplay;
    }

    public String getDesc() {
        return desc;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getCategory(){
        return category;
    }

    public Double getLat() {
        return Lat;
    }

    public Double getLong() {
        return Long;
    }
}
