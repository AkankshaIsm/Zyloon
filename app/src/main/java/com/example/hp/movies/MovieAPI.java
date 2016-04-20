package com.example.hp.movies;

/**
 * Created by hp on 19-04-2016.
 */

import com.example.hp.movies.models.Example;
import com.example.hp.movies.models.MovieModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MovieAPI {
    @GET("/popular?api_key=0877e1cdb4c40aedf0b8c398906658b0")   //returns Example type json object
    void getMovies(Callback<Example> response);

}
