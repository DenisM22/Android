package com.example.movies;

import static com.example.movies.Network.retrofit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainFragment extends Fragment {

    private final ApiService apiService = retrofit.create(ApiService.class);
    private final MovieImagesAdapter2 movieImagesAdapter = new MovieImagesAdapter2();
    private DatabaseHandler db;
    private long getMovieId() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getLong("MOVIE_ID", -1);
        }
        return -1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHandler(requireContext());

        LinearLayout connectionError = view.findViewById(R.id.connection_error);
        ProgressBar loadingProgressBar = view.findViewById(R.id.loading);
        connectionError.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);


        long movieId = getMovieId();
        loadMovieDetails(movieId, connectionError, loadingProgressBar);

        ImageView favoritesFill = view.findViewById(R.id.favorites_fill);

        if (AppConfig.user != null && db.checkMovie(AppConfig.user.id, movieId))
            favoritesFill.setVisibility(View.VISIBLE);
        else
            favoritesFill.setVisibility(View.GONE);
    }

    private void loadMovieDetails(long movieId, View connectionError, ProgressBar loadingProgressBar) {
        Call<MovieDetails> movieDetailsCall = apiService.getMovieDetails(movieId);
        Call<MovieImages> movieImagesCall = apiService.getMovieImages(movieId);

        movieDetailsCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                MovieDetails movieDetails = response.body();
                if (movieDetails != null) {
                    showMovieDetails(movieDetails);
                }
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                connectionError.setVisibility(View.VISIBLE);

                Button update = connectionError.findViewById(R.id.update);
                update.setOnClickListener(v -> {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.mainContainer, MainFragment.this)
                            .commit();
                });
            }
        });

        movieImagesCall.enqueue(new Callback<MovieImages>() {
            @Override
            public void onResponse(Call<MovieImages> call, Response<MovieImages> response) {
                MovieImages movieImages = response.body();
                if (movieImages != null) {
                    List<String> images = new ArrayList<>();
                    for (Image image : movieImages.backdrops) {
                        images.add(image.getImageUrl());
                    }
                    showMovieImages(images, R.id.movie_image_view);
                }
            }

            @Override
            public void onFailure(Call<MovieImages> call, Throwable t) {
            }
        });
    }

    private void showMovieDetails(MovieDetails movieDetails) {
        CardView favorites = requireView().findViewById(R.id.favorites);
        ImageView favoritesFill = requireView().findViewById(R.id.favorites_fill);
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConfig.user != null) {
                    if (!db.checkMovie(AppConfig.user.id, movieDetails.id)) {
                        db.addMovie(AppConfig.user.id, new Movie(movieDetails.id, movieDetails.posterPath));
                        favoritesFill.setVisibility(View.VISIBLE);
                    } else {
                        db.deleteMovie(AppConfig.user.id, new Movie(movieDetails.id, movieDetails.posterPath));
                        favoritesFill.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(requireContext(), "Вход не выполнен", Toast.LENGTH_SHORT).show();
                }

            }
        });

        TextView titleTextView = requireView().findViewById(R.id.title);
        titleTextView.setText(movieDetails.title);

        TextView overviewTextView = requireView().findViewById(R.id.overview);
        overviewTextView.setText(movieDetails.overview);

        TextView originalTitleTextView = requireView().findViewById(R.id.originalTitle);
        originalTitleTextView.setText(movieDetails.originalTitle);

        TextView taglineTextView = requireView().findViewById(R.id.tagline);
        taglineTextView.setText(movieDetails.tagline);

        TextView releaseDateTextView = requireView().findViewById(R.id.releaseDate);
        releaseDateTextView.setText(movieDetails.releaseDate.substring(0, 4));

        TextView runtimeTextView = requireView().findViewById(R.id.runtime);
        runtimeTextView.setText(movieDetails.runtime + " мин.");

        ImageView posterImageView = requireView().findViewById(R.id.poster);
        Glide.with(requireContext())
                .load(movieDetails.getPosterUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(posterImageView);

        List<Genre> genres = movieDetails.genres;
        TextView genresTextView = requireView().findViewById(R.id.genres);
        String genresString = genres.stream()
                .map(Genre::toString)
                .collect(Collectors.joining(", "));
        genresTextView.setText(genresString);
    }

    private void showMovieImages(List<String> images, int idView) {
        RecyclerView imagesContainer = requireView().findViewById(idView);
        imagesContainer.setAdapter(movieImagesAdapter);
        movieImagesAdapter.setImages(images);
    }

}
