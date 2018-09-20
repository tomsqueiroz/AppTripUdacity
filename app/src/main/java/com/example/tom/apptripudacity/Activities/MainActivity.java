package com.example.tom.apptripudacity.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tom.apptripudacity.Adapters.ResultsAdapter;
import com.example.tom.apptripudacity.Interfaces.GetDataService;
import com.example.tom.apptripudacity.Models.Example;
import com.example.tom.apptripudacity.Models.Result;
import com.example.tom.apptripudacity.NetworkUtils.RetrofitClient;
import com.example.tom.apptripudacity.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by tom on 26/08/18.
 */


public class MainActivity extends AppCompatActivity implements ResultsAdapter.ResultsAdapterOnClickHandler {

    private RecyclerView mRecycleView;
    private ResultsAdapter mResultsAdapter;
    private LinearLayoutManager layoutManager;
    private List<Result> results;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private LocationManager lm;
    private ProgressBar pb_main;
    private Context context;
    Map<String, String> map;
    GetDataService service;

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!sharedPreferences.getBoolean(getString(R.string.preference_location_key), true)){
            if(results!=null){
                results.clear();
                mResultsAdapter.notifyDataSetChanged();
            }
            Toast toast = Toast.makeText(this, "Sem Permissão de Localização", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            getPlaces();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!sharedPreferences.getBoolean(getString(R.string.preference_location_key), true)){
            if(results!=null){
                results.clear();
                mResultsAdapter.notifyDataSetChanged();
            }
            Toast toast = Toast.makeText(this, "Sem Permissão de Localização", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            getPlaces();
        }
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
        service = RetrofitClient.getRetrofitInstance().create(GetDataService.class);
        map = new HashMap<>();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        pb_main = (ProgressBar) findViewById(R.id.pb_main);
        context = this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }else if(!sharedPreferences.getBoolean(getString(R.string.preference_location_key), true)) {
            Toast toast = Toast.makeText(this, "Sem Permissão de Localização", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            getPlaces();
        }
    }


    private void getPlaces(){

        editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.preference_location_key), true);
        editor.apply();

        Location location = null;

        try{
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }catch(SecurityException e){
            e.printStackTrace();
        }

        if(location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            map.put("location", latitude + "," + longitude);
            map.put("radius", "5000");

            Call<Example> call = service.getNearbyPlaces(map);
            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Call<Example> call, Response<Example> response) {
                    results = response.body().getResults();
                    mResultsAdapter.setResultList(results);
                    pb_main.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<Example> call, Throwable t) {

                    Toast toast = Toast.makeText(context, "Sem conexão com internet", Toast.LENGTH_SHORT);
                    toast.show();

                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION){
            sharedPreferences = getSharedPreferences(getString(R.string.preference_location_key), Context.MODE_PRIVATE);
            getPlaces();

            }else{

            editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.preference_location_key), false);
            editor.apply();
            Toast toast = Toast.makeText(this, "Sem Permissão de Localização", Toast.LENGTH_SHORT);
            toast.show();

            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbarmain_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
