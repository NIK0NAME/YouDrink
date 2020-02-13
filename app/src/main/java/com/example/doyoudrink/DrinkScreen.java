package com.example.doyoudrink;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.net.URL;

public class DrinkScreen extends AppCompatActivity implements View.OnClickListener {

    public ImageButton exitDrink;
    public Drink drink;
    public ImageView drinkImage;
    public TextView drinkName, drinkDesk, drinkCat, drinkGlass, drnkAlcohol;
    public DbManager dbManager;
    public ImageButton likeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_screen);

        exitDrink = findViewById(R.id.exitDrink);
        drinkImage = findViewById(R.id.drinkImage);
        drinkName = findViewById(R.id.drinkName);
        likeButton = findViewById(R.id.likeButton);
        drinkDesk = findViewById(R.id.drinkDesk);
        drinkCat = findViewById(R.id.drnkCategory);
        drinkGlass = findViewById(R.id.drnkGlass);
        drnkAlcohol = findViewById(R.id.drnkAlcohol);

        exitDrink.setOnClickListener(this);
        likeButton.setOnClickListener(this);

        this.drink = (Drink) getIntent().getSerializableExtra("drink");

        if(AppScreen.user != null) {
            if(AppScreen.dbManager.doLikeDrink(AppScreen.user.id, this.drink.id)) {
                likeButton.setForegroundTintList(ColorStateList.valueOf(Color.RED));
            }else {
                likeButton.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
            }
        }else {
            likeButton.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
        }
        drinkName.setText(drink.name);
        drinkDesk.setText(drink.desc);
        drinkCat.setText(drink.cat);
        drinkGlass.setText(drink.glass);
        if(!this.drink.alcohol.equals("Alcoholic")) {
            drnkAlcohol.setText("");
        }

        Thread h = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream URLcontent = (InputStream) new URL(drink.imgUrl).getContent();
                    //Drawable image = Drawable.createFromStream(URLcontent, "your source link");
                    //Drawable image = RoundedBitmapDrawable.createFromStream(URLcontent, "your source link");
                    final RoundedBitmapDrawable a = RoundedBitmapDrawableFactory.create(getResources(), URLcontent);
                    a.setAntiAlias(true);
                    a.setCornerRadius(10.0f);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drinkImage.setImageDrawable(a);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        h.start();
    }

    @Override
    public void onClick(View view) {
        if(view == exitDrink) {
            finish();
        }else if(view == likeButton) {
            if(AppScreen.user != null) {
                int res = AppScreen.dbManager.likeDrink(AppScreen.user.id, this.drink.id);
                View contextView = findViewById(R.id.drinikScreenContent);
                if(res == 1) {
                    likeButton.setForegroundTintList(ColorStateList.valueOf(Color.RED));
                    Snackbar.make(contextView, "Yo like " + this.drink.name, Snackbar.LENGTH_LONG).show();
                }else if(res == 0) {
                    likeButton.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
                    Snackbar.make(contextView, "Dont like " + this.drink.name + " anymore", Snackbar.LENGTH_LONG).show();
                }
            }else {
                AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                        .setTitle("Hellow there")
                        .setMessage("Need to be loged for this action!")
                        .setPositiveButton("Ok", null)
                        .show();
            }

        }
    }
}
