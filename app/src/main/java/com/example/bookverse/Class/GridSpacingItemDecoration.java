package com.example.bookverse.Class;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;

    public GridSpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        outRect.left = spacing;
        //outRect.right = spacing;
        outRect.top = spacing/2; // Khoảng cách trên cùng cho tất cả các item
        outRect.bottom = spacing/2; // Khoảng cách dưới cùng cho tất cả các item
    }
}
