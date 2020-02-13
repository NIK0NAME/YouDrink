package com.example.doyoudrink;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SearcScreen extends Fragment {

    public ListView searchList;
    public ArrayList<Drink> drkData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.search_screen, container, false);

        searchList = (ListView) view.findViewById(R.id.searchList);

        String [] elems = {};

        ArrayAdapter<String> apd = new ArrayAdapter<>(((AppScreen) getContext()), android.R.layout.simple_list_item_1, elems);
        searchList.setAdapter(apd);

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Drink d = drkData.get(i);
                Intent inte = new Intent(((AppScreen) getContext()), DrinkScreen.class);
                inte.putExtra("drink", d);
                startActivity(inte);
            }
        });
        return view;
    }

    public void setSearch(ArrayList<String> data) {
        ArrayAdapter<String> apd = new ArrayAdapter<>(((AppScreen) getContext()), android.R.layout.simple_list_item_1, data);
        searchList.setAdapter(apd);
    }

    public void makeCoolList(ArrayList<Drink> data) {
        drkData = data;
        ArrayList<HashMap<String, CharSequence>> listItems = new ArrayList<>();
        for (Drink dr : data) {
            HashMap<String, CharSequence> map = new HashMap<>();
            map.put("name", dr.name);
            map.put("cat", dr.cat);
            map.put("al", dr.alcohol);
            listItems.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(((AppScreen) getContext()), listItems, R.layout.search_result,
                new String[]{"name", "cat", "al"},
                new int[]{R.id.searchDrinkName, R.id.txtSerachCat, R.id.searcAlchol});
        searchList.setAdapter(adapter);
    }
}
