package com.example.doyoudrink;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AppScreen extends AppCompatActivity implements View.OnClickListener {
    public static DbManager dbManager;
    public DrawerLayout drawerLayout;
    public static User user;
    public NavigationView navigation;
    public int frPos;
    public TextInputEditText buscador;
    public SearcScreen searcScreen;

    public ImageButton btnMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        btnMenu = findViewById(R.id.btnMenu);
        buscador = findViewById(R.id.buscador);
        AppScreen.user = null;

        btnMenu.setOnClickListener(this);

        searcScreen = null;

        navigation = (NavigationView) findViewById(R.id.navigation);
        navigation.setCheckedItem(0);

        if (navigation != null) {
            setupDrawerContent(navigation);
        }

        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!buscador.getText().toString().equals("")) {
                    if(frPos != 1) {
                        frPos = 1;
                        setFragment(frPos);
                    }
                    AsyncHttpClient client = new AsyncHttpClient();
                    final ArrayList<String> ar = new ArrayList<>();
                    client.get("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + charSequence, null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            try {
                                JSONArray items = new JSONArray();

                                if(!response.getString("drinks").equals("null")) {
                                    items = (JSONArray) response.get("drinks");
                                }


                                for(int i = 0; i < items.length(); i++) {
                                    JSONObject obj = (JSONObject) items.get(i);
                                    //https://www.thecocktaildb.com/api/json/v1/1/search.php?i=vodka
                                    ar.add(obj.getString("strDrink"));
                                }
                                searcScreen.setSearch(ar);
                            } catch (JSONException e) { e.printStackTrace(); }
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {}
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        AppScreen.dbManager = new DbManager(this, "drinkData", null, 1);

        frPos = 0;
        setFragment(frPos);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        final Intent i = new Intent(this, LoginScreen.class);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                        menuItem.setChecked(true);
                        // Crear nuevo fragmento
                        if(menuItem.getItemId() == R.id.nav_log) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                            startActivity(i);
                            //startActivityForResult(i, 20);
                        }else if(menuItem.getItemId() == R.id.search && frPos != 1) {
                            frPos = 1;
                            setFragment(1);
                        }else if(menuItem.getItemId() == R.id.home && frPos != 0) {
                            frPos = 0;
                            setFragment(0);
                        }
                        String title = menuItem.getTitle().toString();
                        //selectItem(title);
                        return true;
                    }
                }
        );
    }

    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        switch (position) {
            case 0:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                MainActivity mns = new MainActivity(this);
                fragmentTransaction.replace(R.id.fragment, mns);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                searcScreen = new SearcScreen();
                fragmentTransaction.replace(R.id.fragment, searcScreen);
                //fragmentTransaction.add(R.id.fragment, mmm);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else if(frPos == 1){
            frPos = 0;
            setFragment(0);
        }else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == btnMenu) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
