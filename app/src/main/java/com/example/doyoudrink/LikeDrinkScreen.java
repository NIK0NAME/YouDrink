package com.example.doyoudrink;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class LikeDrinkScreen extends AppCompatActivity implements FavoritAdapter.ItemClickListener {

    public RecyclerView likeDrinkList;
    public List<Drink> bebidas;
    public FavoritAdapter fAdapter;
    public TextView txtNoLikes;
    public Context cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.like_drink_screen);

        likeDrinkList = findViewById(R.id.likeDrinkList);
        txtNoLikes = findViewById(R.id.txtNoLikes);

        bebidas = new ArrayList<>();

        LinearLayoutManager layoutManager= new LinearLayoutManager(cnt,LinearLayoutManager.VERTICAL, false);
        likeDrinkList.setLayoutManager(layoutManager);

        fAdapter = new FavoritAdapter(this, bebidas, 4);

        fAdapter.setClickListener(this);

        likeDrinkList.setAdapter(fAdapter);

        getLikeDrinks();

    }

    public void getLikeDrinks() {
        Cursor c = AppScreen.dbManager.getLikeDrinks(AppScreen.user.id);
        if (c.moveToFirst()) {
            txtNoLikes.setVisibility(View.GONE);
            //Recorremos el cursor hasta que no haya más registros
            do {
                String id = c.getString(0);
                fillDrinkList(id);
            } while(c.moveToNext());
        }
    }

    public void fillDrinkList(String id) {
        final LikeDrinkScreen ll = this;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=" + id, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    final JSONArray itm = (JSONArray) response.get("drinks");


                    final JSONObject drk = (JSONObject) itm.get(0);
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
                                ll.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bebidas.add(dr);
                                        fAdapter.notifyItemInserted(bebidas.size() - 1);
                                        fAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(View view, int adapterPosition, int position) {
        Drink d = fAdapter.getDataItem(adapterPosition);
        Intent i = new Intent(this, DrinkScreen.class);
        i.putExtra("drink", d);
        startActivity(i);
    }
}
