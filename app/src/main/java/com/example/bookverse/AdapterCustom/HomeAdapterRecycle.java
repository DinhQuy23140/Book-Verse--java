package com.example.bookverse.AdapterCustom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.Class.Book;
import com.example.bookverse.R;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapterRecycle extends RecyclerView.Adapter<HomeAdapterRecycle.Viewholder> {

    private Context context;
    private ArrayList<Book> listBook;

    public HomeAdapterRecycle(Context context, ArrayList<Book> listBook) {
        this.context = context;
        this.listBook = listBook;
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView titleBook;
        ImageView imgBook;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            titleBook = itemView.findViewById(R.id.tvTitle);
            imgBook = itemView.findViewById(R.id.imvItem);
        }
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recycle, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Book itemPosition = listBook.get(position);
        holder.imgBook.setImageResource(itemPosition.getPathImage());
        holder.titleBook.setText(itemPosition.getTitle());
    }

    @Override
    public int getItemCount() {
        return listBook.size();
    }
}


