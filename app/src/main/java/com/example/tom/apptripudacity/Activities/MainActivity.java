package com.example.tom.apptripudacity.Activities;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.tom.apptripudacity.Activities.Models.Example;
import com.example.tom.apptripudacity.Activities.Models.Result;
import com.example.tom.apptripudacity.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by tom on 26/08/18.
 */

public class MainActivity extends AppCompatActivity {

    protected GeoDataClient mGeoDataClient;
    private OkHttpClient httpClient;
    public final String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyAlUvgTV9PolnqpyWUQpMd296BGOJQBY3E&location=-15.756740,-47.868560&radius=5000&keyword=hospital";
    private String resposta;
    private List<Result> results;
    Bitmap bitmap = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);


        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(sm.getSearchableInfo(getComponentName()));

        mGeoDataClient = Places.getGeoDataClient(this, null);
        httpClient = new OkHttpClient();
        results = new ArrayList<>();

        try {
           getPlaces(url);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //getPhotos();




    }

    private void getPlaces(final String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject json = null;
                String responseBodyString;
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    responseBodyString = responseBody.string();

                    try {
                        json = new JSONObject(responseBodyString);
                        String nextPageToken  = parseJson(json.toString());
                        if(nextPageToken != null){
                            getPlaces(url.concat("&pagetoken=" + nextPageToken));
                        }else{  //significa que todos os resultados já estão na lista, prontos para inflar o recyclerview
                            inflateRecycleView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //metodo retorna token para proxima pagina da api
    public String parseJson(String json){
        Gson gson = new GsonBuilder().create();
        Example example = null;

        if(json != null){
            example = gson.fromJson(json, Example.class);
        }
        if(example != null) {
            System.out.println(example.toString());
            results.addAll(example.getResults());
            return example.getNextPageToken();
        }
        return null;
     }

     public void inflateRecycleView(){
        List<Bitmap> bitmapList = new ArrayList<>();
        Bitmap bitmap = null;
        for (Result result : results){
            bitmap = getPhotos(result.getPlaceId());
            bitmapList.add(bitmap);
        }
        //com a lista de fotos, soh inflar a rv
         //notifydatasetchanged


     }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private Bitmap getPhotos(String placeId) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() != 0) {
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                             bitmap = photo.getBitmap();
                        }
                    });
                }
                bitmap = null;
            }
        });
        return bitmap;
    }
}
