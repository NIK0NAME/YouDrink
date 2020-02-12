package com.example.doyoudrink;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Drink implements Serializable {
    public String id;
    public String name;
    transient public Drawable img;
    public String imgUrl;
    public boolean like;
    public String desc;

    public Drink(String id, String name, Drawable img, String url, String desc) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.imgUrl = url;
        this.like = false;
        this.desc = desc;
    }
}
