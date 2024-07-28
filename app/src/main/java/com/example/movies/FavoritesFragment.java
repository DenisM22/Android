package com.example.movies;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private final MovieImagesAdapter movieImagesAdapter = new MovieImagesAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout favoritesNone = view.findViewById(R.id.favorites_none);
        TextView favorites = view.findViewById(R.id.textView6);
        TextView favorites2 = view.findViewById(R.id.textView5);

        favoritesNone.setVisibility(View.VISIBLE);
        favorites.setVisibility(View.GONE);
        favorites2.setVisibility(View.GONE);

        DatabaseHandler db = new DatabaseHandler(requireContext());

        if(AppConfig.user != null) {
            favorites.setVisibility(View.VISIBLE);
            if (!db.isTableMovieEmpty(AppConfig.user.id)) {
                favoritesNone.setVisibility(View.GONE);
                showMovieImages(db.getAllMovies(AppConfig.user.id));
            }
        }
        else
            favorites2.setVisibility(View.VISIBLE);

    }

    private void showMovieImages(List<Movie> movies) {
        RecyclerView imagesContainer = requireView().findViewById(R.id.favorites_scroll);
        imagesContainer.setAdapter(movieImagesAdapter);

        List<Long> ids = new ArrayList<>();
        for (Movie id : movies) {
            ids.add(id.id);
        }

        List<String> images = new ArrayList<>();
        for (Movie image : movies) {
            images.add(image.getPosterUrl());
        }

        movieImagesAdapter.setImages(images, ids);

        // Установка слушателя кликов
        movieImagesAdapter.setOnItemClickListener(new MovieImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Long movieId) {
                MainFragment fragment = new MainFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("MOVIE_ID", movieId);
                fragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }

        });


    }


}
