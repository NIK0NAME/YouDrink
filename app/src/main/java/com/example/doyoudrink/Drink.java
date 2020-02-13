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
    public String cat;
    public String glass;
    public String alcohol;

    public Drink(String id, String name, Drawable img, String url, String desc, String cat, String glass, String al) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.imgUrl = url;
        this.like = false;
        this.desc = desc;
        this.cat = cat;
        this.glass = glass;
        this.alcohol = al;
    }
}
