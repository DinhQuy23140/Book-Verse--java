package com.example.bookverse.AdapterCustom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.bookverse.models.Image;
import com.example.bookverse.R;
import com.example.bookverse.activities.PreviewThemeActivity;

import java.util.List;

public class ThemeAdapter extends ArrayAdapter<Image> {

    private Context context; // Hoặc Activity nếu cần
    private List<Image> listTheme;


    public ThemeAdapter(@NonNull Context context, @NonNull List<Image> ListTheme) {
        super(context, R.layout.item_theme, ListTheme);
        this.context = context;
        this.listTheme = ListTheme;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Image image = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_theme, parent, false);
        }
        ImageView imageview = convertView.findViewById(R.id.itemThemeImage);
        Glide.with(getContext())
                        .load(image.getPath())
                                .placeholder(R.drawable.ic_default_image)
                                        .error(R.drawable.ic_error_load_image)
                                                .into(imageview);
//        imageview.setImageResource(image.getPath());
        Button btnSelectTheme;
        btnSelectTheme = convertView.findViewById(R.id.itemThemeBtnSelect);
        btnSelectTheme.setOnClickListener(view -> {
            Intent previewTheme = new Intent(parent.getContext(), PreviewThemeActivity.class);
            previewTheme.putExtra("pathTheme", image.getPath());
            parent.getContext().startActivity(previewTheme);
        });

        return convertView;
    }
}
