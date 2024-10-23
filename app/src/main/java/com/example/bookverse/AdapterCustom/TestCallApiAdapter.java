package com.example.bookverse.AdapterCustom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.Class.User;
import com.example.bookverse.R;

import java.util.ArrayList;
import java.util.List;

public class TestCallApiAdapter extends RecyclerView.Adapter<TestCallApiAdapter.CustomViewHolder> {
    Context context;
    List<User> listUser;

    public TestCallApiAdapter(Context context, List<User> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_call_api, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        User userPosition = listUser.get(position);
        if(userPosition != null){
            holder.userId.setText(userPosition.getUserId());
            holder.title.setText(userPosition.getTitle());
            holder.body.setText(userPosition.getBody());
        }
    }


    @Override
    public int getItemCount() {
        if (listUser != null){
            return listUser.size();
        }
        return 0;
    }


    static class CustomViewHolder extends RecyclerView.ViewHolder {

        private final TextView userId, title, body;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            userId = itemView.findViewById(R.id.testApiUserIdTv);
            title = itemView.findViewById(R.id.testApiTitleTv);
            body= itemView.findViewById(R.id.testApiBodyTv);
        }
    }
}
