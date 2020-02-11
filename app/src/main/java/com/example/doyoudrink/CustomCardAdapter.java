package com.example.doyoudrink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class CustomCardAdapter extends RecyclerView.Adapter<CustomCardAdapter.MyViewHolder> {
    public Context cnt;
    public List<Drink> datos;
    public ItemClickListener mClickListener;
    public CustomCardAdapter(Context cnt, List datos) {
        this.cnt = cnt;
        this.datos = datos;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drink_card, parent, false);
        return new MyViewHolder(view);
    }

    public Drink getDataItem(int pos) {
        return datos.get(pos);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String animal = null;
        Thread getImg;

        Drink obj = this.datos.get(position);

        holder.card_title.setText(obj.name);
        holder
                .setImage(obj.img);
    }

    @NonNull

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public void setDatos(JSONArray d) {
        //this.datos = d;
    }

    public void clear() {
        //int end = this.datos.length() - 1;
        //this.datos = new JSONArray();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public View itemView;
        public TextView card_title;
        public ImageView card_img;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
            card_title = itemView.findViewById(R.id.card_title);
            card_img = itemView.findViewById(R.id.card_img);
        }

        public void setImage(Drawable image) {
            this.card_img.setImageDrawable(image);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
