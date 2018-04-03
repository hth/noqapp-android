package com.noqapp.android.client.views.toremove;


public class DataModel {


    String name;

    String image;

    public DataModel(String name,  String image) {
        this.name = name;

        this.image=image;
    }


    public String getName() {
        return name;
    }



    public String getImage() {
        return image;
    }


}