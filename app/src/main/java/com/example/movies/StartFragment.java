package com.example.movies;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.movies.Network.retrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartFragment extends Fragment {

    private final ApiService apiService = retrofit.create(ApiService.class);
    private final MovieImagesAdapter movieImagesAdapter1 = new MovieImagesAdapter();
    private final MovieImagesAdapter movieImagesAdapter2 = new MovieImagesAdapter();
    private final MovieImagesAdapter movieImagesAdapter3 = new MovieImagesAdapter();
    private int i = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        return inflater.inflate(R.layout.start_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout connectionError = view.findViewById(R.id.connection_error);
        ProgressBar loadingProgressBar = view.findViewById(R.id.loading);
        CardView favoritesButton = view.findViewById(R.id.favorites_button);

        connectionError.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);
        i = 1;

        startPage("top_rated", R.id.top_rated, movieImagesAdapter1, connectionError, loadingProgressBar);
        startPage("popular", R.id.popular, movieImagesAdapter2, connectionError, loadingProgressBar);
        startPage("upcoming", R.id.upcoming, movieImagesAdapter3, connectionError, loadingProgressBar);

        favoritesButton.setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, new FavoritesFragment())
                .addToBackStack(null)
                .commit());
    }

    private void startPage(String category, int idView, MovieImagesAdapter movieImagesAdapter,
                           View connectionError, View loadingProgressBar) {

        apiService.getMoviesList(category).enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(@NonNull Call<MoviesList> call, @NonNull Response<MoviesList> response) {
                MoviesList moviesList = response.body();
                if (moviesList != null) {

                    List<String> images = new ArrayList<>();
                    for (Movie image : moviesList.results) {
                        images.add(image.getPosterUrl());
                    }

                    List<Long> ids = new ArrayList<>();
                    for (Movie id : moviesList.results) {
                        ids.add(id.id);
                    }

                    showMovieImages(images, idView, movieImagesAdapter, ids);

                    i++;
                    if (i == 3) {
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MoviesList> call, @NonNull Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                connectionError.setVisibility(View.VISIBLE);

                Button update = connectionError.findViewById(R.id.update);
                update.setOnClickListener(v ->
                        getParentFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, new StartFragment())
                        .commit());
            }
        });
    }

    private void showMovieImages(List<String> images, int idView, MovieImagesAdapter movieImagesAdapter, List<Long> ids) {
        RecyclerView imagesContainer = requireView().findViewById(idView);
        imagesContainer.setAdapter(movieImagesAdapter);
        movieImagesAdapter.setImages(images, ids);

        // Установка слушателя кликов
        movieImagesAdapter.setOnItemClickListener(movieId -> {
            MainFragment fragment = new MainFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("MOVIE_ID", movieId);
            fragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null)
                    .commit();
        });
    }


}
