package com.example.tom.apptripudacity.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tom on 12/09/18.
 */

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";



    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
