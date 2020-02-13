package com.example.doyoudrink;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.List;

public class IngridientAdapter extends RecyclerView.Adapter<IngridientAdapter.MyViewHolder> {
    public Context cnt;
    public List<Ingredient> datos;
    public IngridientAdapter.ItemClickListener mClickListener;
    public int count;
    public IngridientAdapter(Context cnt, List datos, int count) {
        this.cnt = cnt;
        this.datos = datos;
        this.count = count;
    }

    @NonNull
    @Override
    public IngridientAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingridient_card, parent, false);
        return new IngridientAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String animal = null;
        Thread getImg;

        Ingredient obj = this.datos.get(position);

        holder.card_title.setText(obj.name);
        holder.setImage(obj.img);
    }

    public Ingredient getDataItem(int pos) {
        return datos.get(pos);
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
        public int cou;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
            card_title = itemView.findViewById(R.id.int_title);
            card_img = itemView.findViewById(R.id.ing_img);
            cou = count;
        }

        public void setImage(Drawable image) {
            this.card_img.setImageDrawable(image);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(), cou);
        }
    }

    void setClickListener(IngridientAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int adapterPosition, int position);
    }

}
