package com.example.doyoudrink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    public TextInputEditText username, pass;
    public MaterialButton goIn, noAcount;
    public CheckBox check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        username = findViewById(R.id.txtUsernameLogin);
        pass = findViewById(R.id.txtPassLogin);
        goIn = findViewById(R.id.btnGoIn);
        check = findViewById(R.id.checkLogin);
        noAcount = findViewById(R.id.btnNoAcount);

        goIn.setOnClickListener(this);
        noAcount.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == goIn) {
            String user = username.getText().toString();
            String p = pass.getText().toString();
            int res = AppScreen.dbManager.makeLogin(user, p);
            View contextView = findViewById(R.id.loginContext);

            if(res >= 0) {
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", "done");
                setResult(2, intent);
                finish();
            }else if(res == -1) {
                Snackbar.make(contextView, "carshin", Snackbar.LENGTH_LONG).show();
            }else {
                username.setText("");
                username.setError("We cant find this datos");
                pass.setText("");
            }
        }else if(view == noAcount) {
            Intent i = new Intent(this, RegisterScreen.class);
            startActivity(i);
        }
    }
}
