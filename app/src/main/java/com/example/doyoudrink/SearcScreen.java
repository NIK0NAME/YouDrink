package com.example.doyoudrink;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SearcScreen extends Fragment {

    public ListView searchList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.search_screen, container, false);

        searchList = (ListView) view.findViewById(R.id.searchList);

        String [] elems = {};

        ArrayAdapter<String> apd = new ArrayAdapter<>(((AppScreen) getContext()), android.R.layout.simple_list_item_1, elems);
        searchList.setAdapter(apd);
        return view;
    }

    public void setSearch(ArrayList<String> data) {
        ArrayAdapter<String> apd = new ArrayAdapter<>(((AppScreen) getContext()), android.R.layout.simple_list_item_1, data);
        searchList.setAdapter(apd);
    }
}
