package com.example.bookverse.AdapterCustom;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookverse.Class.Book;
import com.example.bookverse.Class.Format;
import com.example.bookverse.R;
import com.example.bookverse.activities.InfBokkActivity;
import com.example.bookverse.activities.ViewBookActivity;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeAdapterRecycle extends RecyclerView.Adapter<HomeAdapterRecycle.CustomViewHolder> {

    private Context context;
    private ArrayList<Book> listBook;

    public HomeAdapterRecycle(Context context, ArrayList<Book> listBook) {
        this.context = context;
        this.listBook = listBook;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearData(){
        if(!listBook.isEmpty()){
            listBook.clear();
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recycle, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        PreferenceManager preferenceManager = new PreferenceManager(holder.itemView.getContext());
        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        //Toast.makeText(holder.itemView.getContext(), "Email: " + email, Toast.LENGTH_SHORT).show();
        Book itemPosition = listBook.get(position);
        String urlImage = getUrlImg(itemPosition.getFormats());
        holder.title.setText(itemPosition.getTitle());

//        holder.image.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//                int width = holder.image.getWidth();
//                int height = holder.image.getHeight();
//                Glide.with(holder.itemView.getContext())
//                        .load(urlImage)
//                        .override(width, height)
//                        .placeholder(R.drawable.ic_default_image)
//                        .error(R.drawable.ic_error_load_image)
//                        .into(holder.image);
//            }
//        });

        Glide.with(holder.itemView.getContext())
                .load(urlImage)
                .placeholder(R.drawable.ic_default_image)
                .error(R.drawable.ic_error_load_image)
                .into(holder.image);

        firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                        .document(String.valueOf(itemPosition.getId()))
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful() && task.getResult() != null){
                                List<String> users = (List<String>) task.getResult().get("users");
                                if(users != null){
                                    Set<String> set = new HashSet<>(users);
                                    if(set.contains(email)){
                                        holder.book_favoriteImv.setImageResource(R.drawable.ic_favorite_click);
                                        holder.book_favoriteImv.setActivated(true);
                                    }
                                }
                            }
                        });

        holder.book_favoriteImv.setOnClickListener(view -> {
            if (!holder.book_favoriteImv.isActivated()) {
                holder.book_favoriteImv.setImageResource(R.drawable.ic_favorite_click);
                holder.book_favoriteImv.setActivated(true);
                Map<String, Object> data = new HashMap<>();
                data.put("users", FieldValue.arrayUnion(email));
                firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                        .document(String.valueOf(itemPosition.getId()))
                        .set(data, SetOptions.merge());

            } else {
                holder.book_favoriteImv.setImageResource(R.drawable.ic_favorite);
                holder.book_favoriteImv.setActivated(false);
                firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                        .document(String.valueOf(itemPosition.getId()))
                        .update("users", FieldValue.arrayRemove(email));
            }
        });

        holder.itemView.setOnClickListener(view -> {
            Gson gson = new Gson();
            String json = gson.toJson(itemPosition);
            Intent intent = new Intent(holder.itemView.getContext(), InfBokkActivity.class);
            intent.putExtra("jsonBook", json);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    public String getUrlImg(Map<String, String> format){
        return format.get("image/jpeg");
    }


    @Override
    public int getItemCount() {
        return listBook.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;
        ImageView book_favoriteImv;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitleBook);
            image = itemView.findViewById(R.id.imvItem);
            book_favoriteImv = itemView.findViewById(R.id.book_favoriteImv);
        }
    }
}


