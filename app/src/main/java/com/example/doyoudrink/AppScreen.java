package com.example.doyoudrink;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
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
    public MainActivity mainScreen;
    public MenuItem configuration_section;

    public ImageButton btnMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        btnMenu = findViewById(R.id.btnMenu);
        buscador = findViewById(R.id.buscador);
        configuration_section = findViewById(R.id.configuration_section);

        AppScreen.user = null;




        btnMenu.setOnClickListener(this);

        searcScreen = null;
        mainScreen = new MainActivity(this);

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
                            ArrayList<Drink> drks = new ArrayList<>();
                            try {
                                JSONArray items = new JSONArray();

                                if(!response.getString("drinks").equals("null")) {
                                    items = (JSONArray) response.get("drinks");
                                }


                                for(int i = 0; i < items.length(); i++) {
                                    JSONObject obj = (JSONObject) items.get(i);
                                    //https://www.thecocktaildb.com/api/json/v1/1/search.php?i=vodka
                                    ar.add(obj.getString("strDrink"));
                                    drks.add(new Drink(obj.getString("idDrink"),
                                            obj.getString("strDrink"), null,
                                            obj.getString("strDrinkThumb"),
                                            obj.getString("strInstructions"),
                                            obj.getString("strCategory"),
                                            obj.getString("strGlass"),
                                            obj.getString("strAlcoholic")));
                                }
                                searcScreen.makeCoolList(drks);
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

        AppScreen.dbManager = new DbManager(this, "drinkData", null, 3);

        int saveRes = AppScreen.dbManager.getSaveUser();

        if(saveRes == 1) {
            menuSate();
        }

        setFragment(frPos);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        final Intent i = new Intent(this, LoginScreen.class);
        final Intent i2 = new Intent(this, ProfileScreen.class);
        final Context cnt = this;
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                        menuItem.setChecked(true);
                        // Crear nuevo fragmento
                        if(menuItem.getItemId() == R.id.nav_log) {
                            if(AppScreen.user == null) {
                                drawerLayout.closeDrawer(GravityCompat.START);
                                startActivityForResult(i, 2);
                            }else {
                                new MaterialAlertDialogBuilder(cnt)
                                        .setTitle("Goin home?")
                                        .setMessage("Are you sure you wana log out, sure dude?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                AppScreen.dbManager.removeSaved(AppScreen.user.id);
                                                AppScreen.user = null;
                                                menuSate();

                                                drawerLayout.closeDrawer(GravityCompat.START);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                            }
                            //startActivityForResult(i, 20);
                        }else if(menuItem.getItemId() == R.id.search && frPos != 1) {

                            setFragment(1);
                        }else if(menuItem.getItemId() == R.id.home && frPos != 0) {

                            setFragment(0);
                        }else if(menuItem.getItemId() == R.id.profile) {
                            if(AppScreen.user != null) {
                                startActivityForResult(i2, 3);
                            }else {
                                drawerLayout.closeDrawer(GravityCompat.START);
                                View contextView2 = findViewById(R.id.mainScreenContext);
                                Snackbar.make(contextView2, "Please log in, dude", Snackbar.LENGTH_LONG).show();
                            }

                        }else if(menuItem.getItemId() == R.id.favorite) {
                            Intent inte = new Intent(cnt, LikeDrinkScreen.class);
                            startActivity(inte);
                        }
                        String title = menuItem.getTitle().toString();
                        //selectItem(title);
                        return true;
                    }
                }
        );
    }

    public void menuSate() {
        Menu menu = navigation.getMenu();
        if(AppScreen.user != null) {
            MenuItem menuOpt = menu.findItem(R.id.nav_log);
            menuOpt.setTitle("Log out");

            MenuItem pr = menu.findItem(R.id.profile);
            pr.setTitle(AppScreen.user.name);

            MenuItem menuOpt2 = menu.findItem(R.id.favorite);
            menuOpt2.setVisible(true);
        }else {
            MenuItem menuOpt = menu.findItem(R.id.nav_log);
            menuOpt.setTitle("Iniciar sesion");

            MenuItem menuOpt2 = menu.findItem(R.id.favorite);
            menuOpt2.setVisible(false);

            MenuItem pr = menu.findItem(R.id.profile);
            pr.setTitle("Profile");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2) {
            //login done
            if(data != null) {
                String res = data.getStringExtra("MESSAGE");
                if(res.equals("done")) {

                    menuSate();
                    View contextView = findViewById(R.id.mainScreenContext);
                    Snackbar.make(contextView, "Welcome " + AppScreen.user.name, Snackbar.LENGTH_LONG).show();
                }
            }
        }else if(requestCode == 3) {
            if(data != null) {
                String res = data.getStringExtra("MESSAGE");
                if(res.equals("change")) {

                    menuSate();
                }
            }
        }
    }

    public void setFragment(int position) {
        frPos = position;
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        switch (position) {
            case 0:
                buscador.setText("");
                buscador.clearFocus();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_form_left, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment, mainScreen);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
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

            setFragment(0);
        }else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Exit app")
                    .setMessage("Are you sure you wana EXIT APP?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == btnMenu) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
