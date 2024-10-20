package com.example.bookverse.AdapterCustom;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
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
import com.example.bookverse.activities.ViewBookActivity;

import java.util.ArrayList;

public class HomeAdapterRecycle extends RecyclerView.Adapter<HomeAdapterRecycle.CustomViewHolder> {

    private Context context;
    private ArrayList<Book> listBook;

    public HomeAdapterRecycle(Context context, ArrayList<Book> listBook) {
        this.context = context;
        this.listBook = listBook;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recycle, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Book itemPosition = listBook.get(position);
        holder.title.setText(itemPosition.getTitle());
        holder.image.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                int width = holder.image.getWidth();
                int height = holder.image.getHeight();
                Glide.with(holder.itemView.getContext())
                        .load(itemPosition.getPathImage())
                        .override(width, height)
                        .placeholder(R.drawable.ic_default_image)
                        .error(R.drawable.ic_error_load_image)
                        .into(holder.image);
                holder.image.removeOnLayoutChangeListener(this);
            }
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), ViewBookActivity.class);
            intent.putExtra("book", "valueBook");
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listBook.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitleBook);
            image = itemView.findViewById(R.id.imvItem);
        }
    }
}


