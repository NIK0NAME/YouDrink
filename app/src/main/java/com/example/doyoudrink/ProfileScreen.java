package com.example.doyoudrink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileScreen extends AppCompatActivity implements View.OnClickListener {

    public TextInputEditText user, name, pass;
    public TextView textDrinkNum;
    public LinearLayout butonPlace;
    public MaterialButton btnEditPro, btnSavePro, btnDelAcount;
    public User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        user = findViewById(R.id.proUser);
        name = findViewById(R.id.proName);
        pass = findViewById(R.id.proPass);
        textDrinkNum= findViewById(R.id.textDrinkNum);
        butonPlace = findViewById(R.id.butonPlace);
        btnEditPro = findViewById(R.id.btnEditPro);
        btnSavePro = findViewById(R.id.btnSavePro);
        btnDelAcount = findViewById(R.id.btnDelAcount);

        btnSavePro.setVisibility(View.GONE);

        toggleAll();

        u = AppScreen.user;

        user.setText(u.username);
        name.setText(u.name);
        pass.setText(u.pass);

        btnEditPro.setOnClickListener(this);
        btnSavePro.setOnClickListener(this);
        btnDelAcount.setOnClickListener(this);

        btnDelAcount.setVisibility(View.GONE);

        Cursor c = AppScreen.dbManager.getLikeDrinks(u.id);

        int likes = c.getCount();

        textDrinkNum.setText("" + likes);
    }

    public void toggleAll() {
        boolean state = user.isEnabled();
        user.setEnabled(!state);
        name.setEnabled(!state);
        pass.setEnabled(!state);

        user.setBackgroundColor(Color.WHITE);
        name.setBackgroundColor(Color.WHITE);
        pass.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onClick(View view) {
        if(view == btnEditPro) {
            if(user.isEnabled()) {
                toggleAll();
                btnEditPro.setText("Edit");
                btnDelAcount.setVisibility(View.GONE);
                user.setText(u.username);
                name.setText(u.name);
                pass.setText(u.pass);
                btnSavePro.setVisibility(View.GONE);
            }else {
                toggleAll();
                btnDelAcount.setVisibility(View.VISIBLE);
                btnEditPro.setText("Cancel");
                btnSavePro.setVisibility(View.VISIBLE);
            }
        }else if(view == btnSavePro) {
            View contextView = findViewById(R.id.profileContext);
            String n, u, p;
            n = name.getText().toString();
            u = user.getText().toString();
            p = pass.getText().toString();

            if(!n.equals("") && !u.equals("") && !p.equals("")) {
                int res = AppScreen.dbManager.changeUser(this.u.id, u, p, n);
                if(res == 1) {
                    this.u = AppScreen.user;
                    toggleAll();
                    btnEditPro.setText("Edit");
                    user.setText(this.u.username);
                    name.setText(this.u.name);
                    pass.setText(this.u.pass);
                    btnSavePro.setVisibility(View.GONE);

                    Intent intent = new Intent();
                    intent.putExtra("MESSAGE", "change");
                    setResult(2, intent);

                    Snackbar.make(contextView, "cool change", Snackbar.LENGTH_LONG).show();
                }else {
                    user.setText(this.u.username);
                    name.setText(this.u.name);
                    pass.setText(this.u.pass);
                    Snackbar.make(contextView, "not today", Snackbar.LENGTH_LONG).show();
                }
            }
        }else if(view == btnDelAcount) {
            int res = AppScreen.dbManager.deleteAcount(this.u.id);
            if(res == 1) {
                AppScreen.user = null;
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", "change");
                setResult(2, intent);
                finish();
            }else {

            }
        }
    }
}
