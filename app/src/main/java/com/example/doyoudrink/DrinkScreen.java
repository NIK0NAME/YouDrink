package com.example.doyoudrink;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class DrinkScreen extends AppCompatActivity implements View.OnClickListener {

    public ImageButton exitDrink;
    public Drink drink;
    public ImageView drinkImage;
    public TextView drinkName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_screen);

        exitDrink = findViewById(R.id.exitDrink);
        drinkImage = findViewById(R.id.drinkImage);
        drinkName = findViewById(R.id.drinkName);

        exitDrink.setOnClickListener(this);

        drink = (Drink) getIntent().getSerializableExtra("drink");


        drinkName.setText(drink.name);

        Thread h = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream URLcontent = (InputStream) new URL(drink.imgUrl).getContent();
                    //Drawable image = Drawable.createFromStream(URLcontent, "your source link");
                    //Drawable image = RoundedBitmapDrawable.createFromStream(URLcontent, "your source link");
                    final RoundedBitmapDrawable a = RoundedBitmapDrawableFactory.create(getResources(), URLcontent);
                    a.setAntiAlias(true);
                    a.setCornerRadius(50.0f);
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
        }
    }
}
