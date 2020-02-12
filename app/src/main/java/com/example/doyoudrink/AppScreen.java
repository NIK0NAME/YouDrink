package com.example.doyoudrink;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppScreen extends AppCompatActivity implements View.OnClickListener {
    public static DbManager dbManager;
    public DrawerLayout drawerLayout;
    public static User user;
    public NavigationView navigation;
    public int frPos;

    public ImageButton btnMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        btnMenu = findViewById(R.id.btnMenu);
        AppScreen.user = null;

        btnMenu.setOnClickListener(this);

        navigation = (NavigationView) findViewById(R.id.navigation);
        navigation.setCheckedItem(0);

        if (navigation != null) {
            setupDrawerContent(navigation);
        }

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
                MainScreenFragment mmm = new MainScreenFragment();
                fragmentTransaction.replace(R.id.fragment, mmm);
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
