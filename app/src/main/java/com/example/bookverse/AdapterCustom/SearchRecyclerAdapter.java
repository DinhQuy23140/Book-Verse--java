package com.example.bookverse.AdapterCustom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.R;
import com.example.bookverse.activities.ViewAllRecyclerView;

import java.util.ArrayList;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.CustomHolder> {
    ArrayList<String> listKeySub;
    ArrayList<Integer> listBackgroundPath;
    Context context;

    public SearchRecyclerAdapter(Context context, ArrayList<Integer> listBackgroundPath, ArrayList<String> listKeySub) {
        this.context = context;
        this.listBackgroundPath = listBackgroundPath;
        this.listKeySub = listKeySub;
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_sub, parent, false);
        return new CustomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHolder holder, int position) {
        String keySub = listKeySub.get(position);
        int path = listBackgroundPath.get(position);
        holder.searchSubTitle.setText(keySub);
        holder.searchViewBookTopic.setBackgroundResource(path);
        holder.itemView.setOnClickListener(view1 ->{
            Intent viewResultSearch = new Intent(holder.itemView.getContext(), ViewAllRecyclerView.class);
            viewResultSearch.putExtra("keyView", keySub);
            holder.itemView.getContext().startActivity(viewResultSearch);
            //Toast.makeText(context, keySub, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        if (listKeySub != null){
            return listKeySub.size();
        }
        return 0;
    }

    class CustomHolder extends RecyclerView.ViewHolder {
        TextView searchSubTitle;
        ConstraintLayout searchViewBookTopic;
        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            searchSubTitle = itemView.findViewById(R.id.searchSubKey);
            searchViewBookTopic = itemView.findViewById(R.id.searchViewBookTopic);
        }
    }
}
