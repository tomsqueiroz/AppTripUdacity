package com.example.tom.apptripudacity.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tom.apptripudacity.Adapters.ResultsAdapter;
import com.example.tom.apptripudacity.Interfaces.GetDataService;
import com.example.tom.apptripudacity.Models.Example;
import com.example.tom.apptripudacity.Models.Result;
import com.example.tom.apptripudacity.NetworkUtils.RetrofitClient;
import com.example.tom.apptripudacity.R;
import com.google.android.gms.location.places.GeoDataClient;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by tom on 26/08/18.
 */



public class MainActivity extends AppCompatActivity implements ResultsAdapter.ResultsAdapterOnClickHandler{

    private RecyclerView mRecycleView;
    private ResultsAdapter mResultsAdapter;
    private LinearLayoutManager layoutManager;
    private List<Result> results;

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportActionBar().setTitle(null);


        mRecycleView = (RecyclerView) findViewById(R.id.rv_main);
        layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
        mResultsAdapter = new ResultsAdapter(this, this);
        mRecycleView.setAdapter(mResultsAdapter);


        GetDataService service = RetrofitClient.getRetrofitInstance().create(GetDataService.class);
        Map<String,String> map = new HashMap<>();
        map.put("location", "-15.765079,-47.869921");
        map.put("radius", "5000");
        //map.put("keyword", "hospital");


        Call<Example> call = service.getNearbyPlaces(map);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                results = response.body().getResults();
                mResultsAdapter.setResultList(results);

            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onClick(int position) {
        if(results!=null){
            Intent i = new Intent(this, DetailsActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("resultado", results.get(position));
            i.putExtra("resultado", b);
            startActivity(i);
        }
    }
}
