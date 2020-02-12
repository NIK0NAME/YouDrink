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

import java.io.InputStream;
import java.net.URL;

public class DrinkScreen extends AppCompatActivity implements View.OnClickListener {

    public ImageButton exitDrink;
    public Drink drink;
    public ImageView drinkImage;
    public TextView drinkName, drinkDesk;
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

        exitDrink.setOnClickListener(this);
        likeButton.setOnClickListener(this);

        this.drink = (Drink) getIntent().getSerializableExtra("drink");

        if(AppScreen.user != null) {
            if(AppScreen.dbManager.doLikeDrink(MainActivity.user.id, this.drink.id)) {
                likeButton.setForegroundTintList(ColorStateList.valueOf(Color.RED));
            }else {
                likeButton.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
            }
        }else {
            likeButton.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
        }
        drinkName.setText(drink.name);
        drinkDesk.setText(drink.desc);

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
                int res = AppScreen.dbManager.likeDrink(MainActivity.user.id, this.drink.id);
                if(res == 1) {
                    likeButton.setForegroundTintList(ColorStateList.valueOf(Color.RED));
                }else if(res == 0) {
                    likeButton.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
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
