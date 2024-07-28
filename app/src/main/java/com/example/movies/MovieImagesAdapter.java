package com.example.movies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

class MovieImagesAdapter extends RecyclerView.Adapter<MovieImageViewHolder> {

    private List<String> images = new ArrayList<>();
    private List<Long> ids = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public void setImages(List<String> images, List<Long> ids) {
        this.images.clear();
        this.images.addAll(images);
        this.ids.clear();
        this.ids.addAll(ids);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_for_movies, parent, false);
        return new MovieImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieImageViewHolder holder, int position) {
        String image = images.get(position);
        holder.bind(image);

        holder.itemView.setOnClickListener(v -> {
            Long id = ids.get(position);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Long id);
    }
}

class MovieImagesAdapter2 extends RecyclerView.Adapter<MovieImageViewHolder2> {

    private List<String> images = new ArrayList<>();

    public void setImages(List<String> images) {
        this.images.clear();
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieImageViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_for_images, parent, false);
        return new MovieImageViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieImageViewHolder2 holder, int position) {
        String image = images.get(position);
        holder.bind(image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

}
