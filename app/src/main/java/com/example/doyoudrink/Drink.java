package com.example.doyoudrink;

import android.graphics.drawable.Drawable;

public class Drink {
    public String id;
    public String name;
    public Drawable img;

    public Drink(String id, String name, Drawable img) {
        this.id = id;
        this.name = name;
        this.img = img;
    }
}
