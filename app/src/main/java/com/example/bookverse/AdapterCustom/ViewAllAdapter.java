package com.example.bookverse.AdapterCustom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookverse.Class.Book;
import com.example.bookverse.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ViewAllAdapter extends RecyclerView.Adapter<ViewAllAdapter.CustomViewHolder> {
    ArrayList<Book> listBook;
    Context context;

    public ViewAllAdapter(Context context, ArrayList<Book> listBook) {
        this.context = context;
        this.listBook = listBook;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewall, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Book itemPosion = listBook.get(position);
        holder.title.setText(itemPosion.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(itemPosion.getPathImage())
                .placeholder(R.drawable.loading_ic)
                .error(R.drawable.ic_error_load_image)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listBook.size();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.viewAllTitle);
            imageView = itemView.findViewById(R.id.viewAllImage);
        }
    }
}
