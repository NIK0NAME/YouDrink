package com.example.doyoudrink;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class DrinkScreen extends AppCompatActivity implements View.OnClickListener {

    public ImageButton exitDrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_screen);

        exitDrink = findViewById(R.id.exitDrink);

        exitDrink.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == exitDrink) {
            finish();
        }
    }
}
