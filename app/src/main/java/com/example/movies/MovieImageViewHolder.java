package com.example.movies;

import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

public class MovieImageViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;

    public MovieImageViewHolder(View view) {
        super(view);
        imageView = view.findViewById(R.id.item_image_for_movie);
    }

    public void bind(String imageUrl) {
        Glide.with(imageView).load(imageUrl).into(imageView);
    }
}
