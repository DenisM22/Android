package com.example.movies;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieDetails {

    @SerializedName("id")
    public long id;
    @SerializedName("title")
    public String title;
    @SerializedName("poster_path")
    public String posterPath;
    @SerializedName("overview")
    public String overview;
    @SerializedName("original_title")
    public String originalTitle;
    @SerializedName("tagline")
    public String tagline;
    @SerializedName("release_date")
    public String releaseDate;
    @SerializedName("runtime")
    public String runtime;
    @SerializedName("genres")
    public List<Genre> genres;

    public String getPosterUrl() {
        return AppConfig.IMAGE_BASE_URL + "w342" + posterPath;
    }
}


class Genre {

    @SerializedName("name")
    public String genre;

    @Override
    public String toString() {
        return genre;
    }
}


class MovieImages {

    @SerializedName("backdrops")
    public List<Image> backdrops;
}


class Image {

    @SerializedName("file_path")
    public String filePath;

    public String getImageUrl() {
        return AppConfig.IMAGE_BASE_URL + "w300" + filePath;
    }
}


class MoviesList {

    @SerializedName("results")
    public List<Movie> results;
}

class Movie {

    @SerializedName("id")
    public long id;

    @SerializedName("poster_path")
    public String posterPath;

    public Movie(long id, String posterPath) {
        this.id = id;
        this.posterPath = posterPath;
    }

    public String getPosterUrl() {
        return AppConfig.IMAGE_BASE_URL + "w342" + posterPath;
    }

}
