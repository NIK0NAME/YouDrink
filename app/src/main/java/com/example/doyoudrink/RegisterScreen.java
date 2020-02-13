package com.example.doyoudrink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterScreen extends AppCompatActivity implements View.OnClickListener {

    public TextInputEditText username, pass, name, birth;
    public MaterialButton register, goLogin;
    public CheckBox check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        username = findViewById(R.id.txtUsernameReg);
        pass = findViewById(R.id.txtPassReg);
        name = findViewById(R.id.txtNameReg);
        birth = findViewById(R.id.txtDateReg);

        register = findViewById(R.id.btnRegister);
        goLogin = findViewById(R.id.btnLogIn);

        register.setOnClickListener(this);
        goLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == goLogin) {
            Intent i = new Intent(this, LoginScreen.class);
            startActivity(i);
            finish();
        }else if(view == register) {
            String us, na, pa, da;
            us = username.getText().toString();
            na = name.getText().toString();
            pa = pass.getText().toString();
            da = birth.getText().toString();

            int res = AppScreen.dbManager.addUser(us, pa, na, da);
            View contextView = findViewById(R.id.registerContext);
            if(res == -1) {
                Snackbar.make(contextView, "Somthing did not work on this one", Snackbar.LENGTH_LONG).show();
            }else {
                Snackbar.make(contextView, "Seems user is done", Snackbar.LENGTH_LONG).show();
            }

        }
    }
}
