package com.example.doyoudrink;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class MainActivity extends Fragment implements CustomCardAdapter.ItemClickListener, View.OnClickListener {

    //COLOR
    //https://colorhunt.co/palette/171238

    public JSONObject object;
    public TextInputEditText buscador;
    public LinearLayout firstSlide;
    public RecyclerView meet_a_drink_list, popular_drink_list, ingre_list;
    public CustomCardAdapter cardAdapter, popularAdapter;
    public IngridientAdapter ingridintAdapter;
    public NavigationView navigation;
    public JSONArray randomDrinks;
    public ImageButton reloadRandoms, btnMenu;
    public List<Drink> bebidas, popularDrinks;
    public List<Ingredient> ingredientes;
    public static DbManager dbManager;
    public DrawerLayout drawerLayout;
    public static User user;
    public Context cnt;

    public MainActivity(Context c) {
        cnt = c;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_screen, container, false);

        meet_a_drink_list = (RecyclerView) view.findViewById(R.id.meet_a_drink_list);
        reloadRandoms = (ImageButton) view.findViewById(R.id.reloadRandoms);
        popular_drink_list = (RecyclerView) view.findViewById(R.id.popular_drink_list);
        ingre_list = (RecyclerView) view.findViewById(R.id.ingre_list);
        //btnMenu = findViewById(R.id.btnMenu);

        reloadRandoms.setOnClickListener(this);
        //btnMenu.setOnClickListener(this);

        randomDrinks = new JSONArray();
        randomDrinks.put(new JSONObject());

        MainActivity.user = null;

        bebidas = new ArrayList<>();
        popularDrinks= new ArrayList<>();
        ingredientes = new ArrayList<>();

        LinearLayoutManager layoutManager= new LinearLayoutManager(cnt,LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2= new LinearLayoutManager(cnt,LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager3= new LinearLayoutManager(cnt,LinearLayoutManager.HORIZONTAL, false);
        meet_a_drink_list.setLayoutManager(layoutManager);
        popular_drink_list.setLayoutManager(layoutManager2);
        ingre_list.setLayoutManager(layoutManager3);

        cardAdapter = new CustomCardAdapter(cnt, bebidas, 0);
        popularAdapter = new CustomCardAdapter(cnt, popularDrinks, 1);
        ingridintAdapter = new IngridientAdapter(cnt, ingredientes, 2);

        cardAdapter.setClickListener(this);
        popularAdapter.setClickListener(this);

        meet_a_drink_list.setAdapter(cardAdapter);
        popular_drink_list.setAdapter(popularAdapter);
        ingre_list.setAdapter(ingridintAdapter);

        randomDrinks = new JSONArray();
        fillRandomDrinks();
        getSomeIngridients();
        fillPopularDrinks();

        return view;
    }

    public void throwSimpleAlert(String msg) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(cnt)
                .setTitle("Simple alert")
                .setMessage(msg)
                .setPositiveButton("Ok", null)
                .show();
    }

    public void fillPopularDrinks() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    final JSONArray itm = (JSONArray) response.get("drinks");

                    for(int i = 0; i < 15; i++) {
                        final JSONObject drk = (JSONObject) itm.get(i);
                        Drink d = new Drink(drk.getString("idDrink"),
                                drk.getString("strDrink"), null,
                                drk.getString("strDrinkThumb"),
                                drk.getString("strInstructions"),
                                drk.getString("strCategory"),
                                drk.getString("strGlass"),
                                drk.getString("strAlcoholic"));
                        final Drink dr = d;

                        Thread h = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    InputStream URLcontent = (InputStream) new URL(drk.get("strDrinkThumb").toString()).getContent();
                                    //Drawable image = Drawable.createFromStream(URLcontent, "your source link");
                                    //Drawable image = RoundedBitmapDrawable.createFromStream(URLcontent, "your source link");
                                    RoundedBitmapDrawable a = RoundedBitmapDrawableFactory.create(getResources(), URLcontent);
                                    //a.setCornerRadius(400.0f);
                                    a.setAntiAlias(true);
                                    a.setCircular(true);
                                    dr.img = a;
                                    ((AppScreen) getActivity()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            popularDrinks.add(dr);
                                            popularAdapter.notifyItemInserted(popularDrinks.size() - 1);
                                            popularAdapter.notifyDataSetChanged();
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        h.start();
                    }

                } catch (JSONException e) { e.printStackTrace(); }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {}
        });
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
                                            itm.getString("strInstructions"),
                                            itm.getString("strCategory"),
                                            itm.getString("strGlass"),
                                            itm.getString("strAlcoholic"));
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
                                    ((AppScreen) getActivity()).runOnUiThread(new Runnable() {
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

                                    final Ingredient ing = new Ingredient(items.getString("idIngredient"),
                                                items.getString("strIngredient"));

                                    Thread h = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                InputStream URLcontent = (InputStream) new URL("https://www.thecocktaildb.com/images/ingredients/" + ing.name + ".png").getContent();
                                                //Drawable image = Drawable.createFromStream(URLcontent, "your source link");
                                                //Drawable image = RoundedBitmapDrawable.createFromStream(URLcontent, "your source link");
                                                RoundedBitmapDrawable a = RoundedBitmapDrawableFactory.create(getResources(), URLcontent);
                                                //a.setCornerRadius(400.0f);
                                                a.setAntiAlias(true);
                                                a.setCircular(true);
                                                ing.img = a;
                                                ((AppScreen) getActivity()).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ingredientes.add(ing);
                                                        ingridintAdapter.notifyItemInserted(bebidas.size() - 1);
                                                        ingridintAdapter.notifyDataSetChanged();
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
                    }

                } catch (JSONException e) { e.printStackTrace(); }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {}
        });
    }

    @Override
    public void onItemClick(View view, int position, int c) {
        Drink d = popularAdapter.getDataItem(position);
        if(c == 0) {
            d = cardAdapter.getDataItem(position);
        }else if(c == 1) {
            d = popularAdapter.getDataItem(position);
        }
        /*AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Team " + position + "cliked")
                .setMessage("Drink " + d.name)
                .setPositiveButton("Ok", null)
                .show();*/
        Intent i = new Intent(cnt, DrinkScreen.class);
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
        }else if(view == btnMenu) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}