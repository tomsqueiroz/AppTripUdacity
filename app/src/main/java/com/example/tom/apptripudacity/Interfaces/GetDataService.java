package com.example.tom.apptripudacity.Interfaces;

import com.example.tom.apptripudacity.Models.Example;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by tom on 12/09/18.
 */

public interface GetDataService {

    @GET("nearbysearch/json?key=YOUR API KEY HERE")
    Call<Example> getNearbyPlaces(@QueryMap Map<String, String> options);

}
