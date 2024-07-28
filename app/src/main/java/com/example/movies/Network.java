package com.example.movies;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class Network {

    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(AppConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
