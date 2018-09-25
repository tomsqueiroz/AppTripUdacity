package com.example.tom.apptripudacity.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tom.apptripudacity.Adapters.ResultsAdapter;
import com.example.tom.apptripudacity.Data.PlaceContract;
import com.example.tom.apptripudacity.Interfaces.GetDataService;
import com.example.tom.apptripudacity.Models.Example;
import com.example.tom.apptripudacity.Models.Geometry;
import com.example.tom.apptripudacity.Models.Photo;
import com.example.tom.apptripudacity.Models.Result;
import com.example.tom.apptripudacity.Network.NetworkUtils;
import com.example.tom.apptripudacity.Network.RetrofitClient;
import com.example.tom.apptripudacity.R;
import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by tom on 26/08/18.
 */


public class MainActivity extends AppCompatActivity implements ResultsAdapter.ResultsAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecycleView;
    private ResultsAdapter mResultsAdapter;
    private LinearLayoutManager layoutManager;
    private List<Result> results;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int ID_PLACES_LOADER = 24;
    private AlertDialog dialog;
    private LocationManager lm;
    private ProgressBar pb_main;
    private Context context;
    Map<String, String> map;
    GetDataService service;
    private AdView mAdView;




    /***********************INDEX DO CURSOR*************/
    public static final int INDEX_ID_BANCO = 0;
    public static final int INDEX_NOME = 1;
    public static final int INDEX_LAT = 2;
    public static final int INDEX_LNG = 3;
    public static final int INDEX_ID_GOOGLE = 4;
    public static final int INDEX_PHOTOS = 5;
    public static final int INDEX_RATING = 6;
    /***************************************************/


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(results!=null)
            results.clear();
        mResultsAdapter.notifyDataSetChanged();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!sharedPreferences.getBoolean(getString(R.string.preference_location_key), true)) {
            Toast toast = Toast.makeText(this, "Sem Permissão de Localização", Toast.LENGTH_SHORT);
            toast.show();
            callLoaderManager();
        } else{
            if(NetworkUtils.connection_ok(this)) {
                pb_main.setVisibility(View.VISIBLE);
                getPlaces(null);
            } else if(!NetworkUtils.connection_ok(this)){
                Toast toast = Toast.makeText(this, "Sem Conexão com a Internet", Toast.LENGTH_SHORT);
                toast.show();
                callLoaderManager();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportActionBar().setTitle(null);
        Stetho.initializeWithDefaults(this);

        mRecycleView = findViewById(R.id.rv_main);
        layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
        mResultsAdapter = new ResultsAdapter(this, this);
        mRecycleView.setAdapter(mResultsAdapter);
        service = RetrofitClient.getRetrofitInstance().create(GetDataService.class);
        map = new HashMap<>();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        pb_main = findViewById(R.id.pb_main);
        pb_main.setVisibility(View.VISIBLE);
        context = this;



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Uri deleteUri = PlaceContract.PlaceEntry.CONTENT_URI;
                        ContentResolver contentResolver = getContentResolver();
                        contentResolver.delete(deleteUri, null, null);
                        callLoaderManager();
                    }
                });
        builder.setNegativeButton(R.string.dialog_nao, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

            }
        });

        dialog = builder.create();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }else if(!sharedPreferences.getBoolean(getString(R.string.preference_location_key), true)) {
            Toast toast = Toast.makeText(this, "Sem Permissão de Localização", Toast.LENGTH_SHORT);
            toast.show();
            callLoaderManager();
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && sharedPreferences.getBoolean(getString(R.string.preference_location_key), true)){

            if(NetworkUtils.connection_ok(this)) {
                getPlaces(null);

            } else if(!NetworkUtils.connection_ok(this)){
                Toast toast = Toast.makeText(this, "Sem Conexão com a Internet", Toast.LENGTH_SHORT);
                toast.show();
                callLoaderManager();
            }
        }
    }

    public void callLoaderManager(){
        getSupportLoaderManager().initLoader(ID_PLACES_LOADER, null, this);
    }


    private void getPlaces(String query){


        if(query==null){
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
                if(results != null)
                    results.clear();
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
            }else{
                if(results != null)
                    results.clear();
                double latitude =  -15.4647;
                double longitude = -47.5547;
                map.put("location", latitude + "," + longitude);
                map.put("radius", "5000");

                Call<Example> call = service.getNearbyPlaces(map);
                call.enqueue(new Callback<Example>() {
                    @Override
                    public void onResponse(Call<Example> call, Response<Example> response) {
                        results = response.body().getResults();
                        if(results!=null){
                            mResultsAdapter.setResultList(results);
                        }
                        pb_main.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<Example> call, Throwable t) {

                        Toast toast = Toast.makeText(context, "Sem conexão com internet", Toast.LENGTH_SHORT);
                        toast.show();

                    }
                });
            }

        }else{

            Location location = null;

            try{
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }catch(SecurityException e){
                e.printStackTrace();
            }

            if(location != null) {
                if(results != null)
                    results.clear();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                map.put("location", latitude + "," + longitude);
                map.put("radius", "5000");
                map.put("keyword", query);

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
            }else{
                if(results != null)
                    results.clear();
                double latitude =  -15.4647;
                double longitude = -47.5547;
                map.put("location", latitude + "," + longitude);
                map.put("radius", "5000");
                map.put("keyword", query);

                Call<Example> call = service.getNearbyPlaces(map);
                call.enqueue(new Callback<Example>() {
                    @Override
                    public void onResponse(Call<Example> call, Response<Example> response) {
                        results = response.body().getResults();
                        if(results!=null){
                            mResultsAdapter.setResultList(results);
                        }
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
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION){
            sharedPreferences = getSharedPreferences(getString(R.string.preference_location_key), Context.MODE_PRIVATE);
            if(NetworkUtils.connection_ok(this)){
                getPlaces(null);
            }else{
                Toast toast = Toast.makeText(this, "Sem Conexão com a Internet", Toast.LENGTH_SHORT);
                toast.show();

            }
        }else{

            editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.preference_location_key), false);
            editor.apply();
            Toast toast = Toast.makeText(this, "Sem Permissão de Localização", Toast.LENGTH_SHORT);
            toast.show();
            callLoaderManager();
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbarmain_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                results.clear();
                mResultsAdapter.notifyDataSetChanged();

                if(NetworkUtils.connection_ok(context)) {
                    getPlaces(query);

                } else if(!NetworkUtils.connection_ok(context)){
                    Toast toast = Toast.makeText(context, "Sem Conexão com a Internet", Toast.LENGTH_SHORT);
                    toast.show();
                    callLoaderManager();
                }
                return  true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }


        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivity(intent);
                break;
            case R.id.action_deleteall:
                dialog.show();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


    @Override
    public void onClick(int position) {
        if(results!=null && !results.isEmpty()){
            Intent i = new Intent(this, DetailsActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("resultado", results.get(position));
            i.putExtra("resultado", b);
            startActivity(i);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id){

            case ID_PLACES_LOADER:

                Uri queryUri = PlaceContract.PlaceEntry.CONTENT_URI;
                return new CursorLoader(this,
                        queryUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Unknown Loader: " + id);

        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        pb_main.setVisibility(View.INVISIBLE);
        results = new ArrayList<>();
        if(data != null){
            data.moveToFirst();
            if(data.getCount()!=0) {
                while (!data.isAfterLast()) {
                    Result result = new Result();
                    result.setName(data.getString(INDEX_NOME));
                    Geometry geometry = new Geometry();
                    com.example.tom.apptripudacity.Models.Location location = new com.example.tom.apptripudacity.Models.Location();
                    location.setLat(data.getDouble(INDEX_LAT));
                    location.setLng(data.getDouble(INDEX_LNG));
                    geometry.setLocation(location);
                    result.setGeometry(geometry);
                    result.setPlaceId(data.getString(INDEX_ID_GOOGLE));
                    List<Photo> photos = new ArrayList<>();
                    Photo photo = new Photo();
                    photo.setPhotoReference(data.getString(INDEX_PHOTOS));
                    photos.add(photo);
                    result.setPhotos(photos);
                    result.setRating(data.getFloat(INDEX_RATING));
                    results.add(result);
                    data.moveToNext();
                }
            }
        }
        mResultsAdapter.setResultList(results);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        results.clear();
        mResultsAdapter.notifyDataSetChanged();
    }
}
