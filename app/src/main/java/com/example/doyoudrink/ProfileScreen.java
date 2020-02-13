package com.example.doyoudrink;

import androidx.appcompat.app.AppCompatActivity;

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
    public MaterialButton btnEditPro, btnSavePro;
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

        btnSavePro.setVisibility(View.GONE);

        toggleAll();

        u = AppScreen.user;

        user.setText(u.username);
        name.setText(u.name);
        pass.setText(u.pass);

        btnEditPro.setOnClickListener(this);
        btnSavePro.setOnClickListener(this);

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
                user.setText(u.username);
                name.setText(u.name);
                pass.setText(u.pass);
                btnSavePro.setVisibility(View.GONE);
            }else {
                toggleAll();
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
                    Snackbar.make(contextView, "cool change", Snackbar.LENGTH_LONG).show();
                }else {
                    user.setText(this.u.username);
                    name.setText(this.u.name);
                    pass.setText(this.u.pass);
                    Snackbar.make(contextView, "not today", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }
}
