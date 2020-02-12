package com.example.doyoudrink;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Ingredient implements Serializable {
    public String id;
    public String name;
    public String urlImg;
    transient public Drawable img;

    public Ingredient(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
