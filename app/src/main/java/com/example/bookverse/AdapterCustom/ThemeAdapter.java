package com.example.bookverse.AdapterCustom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookverse.Class.Image;
import com.example.bookverse.R;

import java.util.List;

public class ThemeAdapter extends ArrayAdapter<Image> {
    public ThemeAdapter(@NonNull Context context, @NonNull List<Image> ListTheme) {
        super(context, R.layout.item_theme, ListTheme);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Image image = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme, parent, false);
        }
        ImageView imageview = convertView.findViewById(R.id.itemThemeImage);
        imageview.setImageResource(image.getPath());
        return convertView;
    }
}
