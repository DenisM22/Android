package com.example.movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    //получить информацию о фильме по id
    @GET("movie/{movie_id}?language=ru")
    Call<MovieDetails> getMovieDetails(
            @Path("movie_id") long movieId
    );

    //получить список изображений для фильма по id
    @GET("movie/{movie_id}/images?include_image_language=en,null")
    Call<MovieImages> getMovieImages(
            @Path("movie_id") long movieId
    );

    //получить список фильмов
    @GET("movie/{category}")
    Call<MoviesList> getMoviesList(
            @Path("category") String category
    );
}
