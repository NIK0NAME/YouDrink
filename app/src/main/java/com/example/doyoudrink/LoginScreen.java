package com.example.doyoudrink;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    public TextInputEditText username, pass;
    public MaterialButton goIn;
    public CheckBox check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        username = findViewById(R.id.txtUsernameLogin);
        pass = findViewById(R.id.txtPassLogin);
        goIn = findViewById(R.id.btnGoIn);
        check = findViewById(R.id.checkLogin);

        goIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == goIn) {

        }
    }
}
