package com.example.bookverse.AdapterCustom;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookverse.models.Book;
import com.example.bookverse.models.Person;
import com.example.bookverse.R;
import com.example.bookverse.activities.InfBokkActivity;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

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

    @SuppressLint("NotifyDataSetChanged")
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

            String urlImg = getUrlImg(itemPosition.getFormats());
            Glide.with(holder.itemView.getContext())
                    .load(urlImg)
                    .placeholder(R.drawable.ic_default_image)
                    .error(R.drawable.ic_error_load_image)
                    .into(holder.bookImage);

            holder.downCount.setText(String.valueOf(itemPosition.getDownload_count()));

            holder.itemView.setOnClickListener(FlowPreview ->{
                Intent viewBook = new Intent(holder.itemView.getContext(), InfBokkActivity.class);
                Gson gson = new Gson();
                String json = gson.toJson(itemPosition);
                viewBook.putExtra("jsonBook", json);
                holder.itemView.getContext().startActivity(viewBook);
            });

            holder.btnDelete.setOnClickListener(view->{
                PreferenceManager preferenceManager = new PreferenceManager(holder.itemView.getContext());
                String email = preferenceManager.getString(Constants.KEY_EMAIL);
                Toast.makeText(holder.itemView.getContext(), "Email: " + email, Toast.LENGTH_SHORT).show();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection(Constants.KEY_COLLECTION_RECENTREAD)
                        .document(email)
                        .update("BookId", FieldValue.arrayRemove(itemPosition.getId()))
                        .addOnCompleteListener(task -> {
                           listBookrecent.remove(position);
                           notifyDataSetChanged();
                        });
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
        TextView bookTitle, bookAuthor, downCount;
        ImageView bookImage, btnDelete;

        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            bookAuthor = itemView.findViewById(R.id.search_authorstv);
            bookTitle = itemView.findViewById(R.id.search_titlebooktv);
            downCount = itemView.findViewById(R.id.search_downcounttv);
            bookImage = itemView.findViewById(R.id.search_image_iv);
            btnDelete = itemView.findViewById(R.id.search_btndeletehis);
        }
    }
    public String getUrlImg(Map<String, String> format){
        return format.get("image/jpeg");
    }
}
