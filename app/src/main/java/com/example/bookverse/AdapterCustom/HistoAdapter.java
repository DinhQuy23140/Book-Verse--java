package com.example.bookverse.AdapterCustom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.Class.Book;
import com.example.bookverse.Class.Person;
import com.example.bookverse.R;
import com.example.bookverse.activities.InfBokkActivity;

import java.util.ArrayList;

public class HistoAdapter extends RecyclerView.Adapter<HistoAdapter.CustomHolder> {
    ArrayList<Book> listBookrecent;
    Context context;

    public HistoAdapter(Context context, ArrayList<Book> listBookrecent) {
        this.context = context;
        this.listBookrecent = listBookrecent;
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_view, parent, false);
        return new CustomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHolder holder, int position) {
        Book itemPosition = listBookrecent.get(position);
        String listAuthor_str = "";
        if(itemPosition != null){
            holder.bookTitle.setText(itemPosition.getTitle());
            ArrayList<Person> listAuthor = itemPosition.getAuthors();
            for(int i = 0 ;i < listAuthor.size(); i++){
                listAuthor_str += listAuthor.get(i).getName();
                if(i < listAuthor.size()) listAuthor_str += ", ";
            }
            holder.bookAuthor.setText(listAuthor_str);

            holder.itemView.setOnClickListener(FlowPreview ->{
                Intent viewBook = new Intent(holder.itemView.getContext(), InfBokkActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bundleBook", itemPosition);
                viewBook.putExtra("intentBundle", bundle);
                holder.itemView.getContext().startActivity(viewBook);
            });

            holder.btnDelete.setOnClickListener(view->{
                listBookrecent.remove(position);
                return;
            });
        }
    }

    @Override
    public int getItemCount() {
        if(listBookrecent != null){
            return listBookrecent.size();
        }
        return 0;
    }


    class CustomHolder extends RecyclerView.ViewHolder {
        TextView bookTitle, bookAuthor;
        ImageView bookImage, btnDelete;

        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            bookAuthor = itemView.findViewById(R.id.search_authorstv);
            bookTitle = itemView.findViewById(R.id.search_titlebooktv);
            bookImage = itemView.findViewById(R.id.search_image_iv);
            btnDelete = itemView.findViewById(R.id.search_btndeletehis);
        }
    }
}
