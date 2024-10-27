package com.example.bookverse.AdapterCustom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.R;

import java.util.ArrayList;

public class KeyRelativeAdapter extends RecyclerView.Adapter<KeyRelativeAdapter.CustomHolder> {
    ArrayList<String> listKey;
    ArrayList<Integer> listPath;
    Context context;

    public KeyRelativeAdapter(Context context, ArrayList<String> listKey, ArrayList<Integer> listPath) {
        this.context = context;
        this.listKey = listKey;
        this.listPath = listPath;
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_relative, parent, false);
        return new CustomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHolder holder, int position) {
        String key = listKey.get(position);
        int path = listPath.get(position);
        if (key != null){
            holder.keySearch.setText(key);
            holder.background.setBackgroundResource(path);
        }
    }

    @Override
    public int getItemCount() {
        if(listKey != null){
            return listKey.size();
        }
        return 0;
    }

    class CustomHolder extends RecyclerView.ViewHolder {
        TextView keySearch;
        LinearLayout background;
        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            keySearch = itemView.findViewById(R.id.search_keyRelative);
            background = itemView.findViewById(R.id.search_backroundRelative);
        }
    }
}
