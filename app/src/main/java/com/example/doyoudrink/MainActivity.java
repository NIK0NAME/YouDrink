package com.example.doyoudrink;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.*;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements CustomCardAdapter.ItemClickListener, View.OnClickListener {

    //COLOR
    //https://colorhunt.co/palette/171238

    public JSONObject object;
    public TextInputEditText buscador;
    public LinearLayout firstSlide;
    public RecyclerView meet_a_drink_list;
    public CustomCardAdapter cardAdapter, ingridintAdapter;
    public NavigationView navigation;
    public JSONArray randomDrinks;
    public ImageButton reloadRandoms;
    public List<Drink> bebidas;
    public List<Ingredient> ingredientes;
    public static DbManager dbManager;
    public DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buscador = findViewById(R.id.buscador);
        meet_a_drink_list = findViewById(R.id.meet_a_drink_list);
        reloadRandoms = findViewById(R.id.reloadRandoms);
        drawerLayout = findViewById(R.id.drawer_layout);

        reloadRandoms.setOnClickListener(this);

        randomDrinks = new JSONArray();
        randomDrinks.put(new JSONObject());

        bebidas = new ArrayList<>();
        ingredientes = new ArrayList<>();

        navigation = (NavigationView) findViewById(R.id.navigation);
        navigation.setCheckedItem(0);

        if (navigation != null) {
            setupDrawerContent(navigation);
        }

        MainActivity.dbManager = new DbManager(this, "drinkData", null, 1);

        /*AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Accion DB")
                .setMessage(msg)
                .setPositiveButton("Ok", null)
                .show();*/

        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        meet_a_drink_list.setLayoutManager(layoutManager);

        cardAdapter = new CustomCardAdapter(this, bebidas);
        cardAdapter.setClickListener(this);
        meet_a_drink_list.setAdapter(cardAdapter);

        randomDrinks = new JSONArray();
        fillRandomDrinks();
        getSomeIngridients();
    }

    public void throwSimpleAlert(String msg) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Simple alert")
                .setMessage(msg)
                .setPositiveButton("Ok", null)
                .show();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        final Intent i = new Intent(this, LoginScreen.class);
        throwSimpleAlert("entra");
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                        throwSimpleAlert("se clikooo");
                        menuItem.setChecked(true);
                        // Crear nuevo fragmento
                        if(menuItem.getItemId() == R.id.nav_log) {
                            throwSimpleAlert("intentando abrir login");
                            startActivity(i);
                            //startActivityForResult(i, 20);
                        }
                        String title = menuItem.getTitle().toString();
                        //selectItem(title);
                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.left_menu, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        throwSimpleAlert(item.toString());
        if(item.getItemId() == R.id.nav_log) {
            /*Intent i = new Intent(this, LoginScreen.class);
            startActivity(i);*/
            drawerLayout.openDrawer(GravityCompat.START);
        }
        /*switch (item.getItemId()) {
            case android.R.id.home:
                throwSimpleAlert("intentando abrir drawer");
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    public void fillRandomDrinks() {
        if(bebidas.size() < 12) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("https://www.thecocktaildb.com/api/json/v1/1/random.php", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        final JSONObject itm = (JSONObject) ((JSONArray) response.get("drinks")).get(0);
                        Drink d = new Drink(itm.getString("idDrink"),
                                            itm.getString("strDrink"), null,
                                            itm.getString("strDrinkThumb"),
                                            itm.getString("strInstructions"));
                        final Drink dr = d;

                        Thread h = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    InputStream URLcontent = (InputStream) new URL(itm.get("strDrinkThumb").toString()).getContent();
                                    //Drawable image = Drawable.createFromStream(URLcontent, "your source link");
                                    //Drawable image = RoundedBitmapDrawable.createFromStream(URLcontent, "your source link");
                                    RoundedBitmapDrawable a = RoundedBitmapDrawableFactory.create(getResources(), URLcontent);
                                    //a.setCornerRadius(400.0f);
                                    a.setAntiAlias(true);
                                    a.setCircular(true);
                                    dr.img = a;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            bebidas.add(dr);
                                            cardAdapter.notifyItemInserted(bebidas.size() - 1);
                                            cardAdapter.notifyDataSetChanged();
                                            fillRandomDrinks();
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        h.start();

                    } catch (JSONException e) { e.printStackTrace(); }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {}
            });
        }else {
            //makeCard(randomDrinks);
        }
    }

    public void getSomeIngridients() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://www.thecocktaildb.com/api/json/v1/1/list.php?i=list", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    final JSONArray items = (JSONArray) response.get("drinks");

                    for(int i = 0; i < 13; i++) {
                        JSONObject obj = (JSONObject) items.get(i);
                        //https://www.thecocktaildb.com/api/json/v1/1/search.php?i=vodka
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.get("https://www.thecocktaildb.com/api/json/v1/1/search.php?i=" + obj.getString("strIngredient1"), null, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                try {
                                final JSONObject items = (JSONObject) ((JSONArray) response.get("ingredients")).get(0);
                                ingredientes.add(new Ingredient(items.getString("idIngredient"),
                                                                items.getString("strIngredient")));

                                } catch (JSONException e) { e.printStackTrace(); }
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {}
                        });
                    }

                } catch (JSONException e) { e.printStackTrace(); }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {}
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Drink d = cardAdapter.getDataItem(position);
        /*AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Team " + position + "cliked")
                .setMessage("Drink " + d.name)
                .setPositiveButton("Ok", null)
                .show();*/
        Intent i = new Intent(this, DrinkScreen.class);
        i.putExtra("drink", d);
        startActivity(i);
    }

    @Override
    public void onClick(View view) {
        if(view == reloadRandoms) {
            int fin = bebidas.size();
            bebidas.clear();
            cardAdapter.notifyItemRangeRemoved(0, fin);
            cardAdapter.notifyDataSetChanged();
            fillRandomDrinks();
        }
    }
}