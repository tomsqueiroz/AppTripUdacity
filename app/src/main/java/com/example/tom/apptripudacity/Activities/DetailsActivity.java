package com.example.tom.apptripudacity.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.tom.apptripudacity.Data.PlaceContract;
import com.example.tom.apptripudacity.Models.Geometry;
import com.example.tom.apptripudacity.Models.Location;
import com.example.tom.apptripudacity.Models.OpeningHours;
import com.example.tom.apptripudacity.Models.Result;
import com.example.tom.apptripudacity.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by tom on 05/09/18.
 */

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mMap;
    private Double lat = -15.7801;
    private Double lng = -47.9292;
    private String name = "Brasília";
    private Boolean openNow;
    private Float rating;
    private TextView tv_details_nome;
    private TextView tv_details_opennow;
    private TextView tv_details_rating;
    private CheckBox cb_save;
    private Result result = null;
    private AlertDialog dialog;
    private String placeId;
    private static final int ID_PLACES_LOADER = 24;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cb_save = findViewById(R.id.cb_save);
        tv_details_nome = findViewById(R.id.tv_details_nome);
        tv_details_opennow =  findViewById(R.id.tv_details_opennow);
        tv_details_rating =  findViewById(R.id.tv_details_rating);
        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("resultado");
        result = (Result) b.getSerializable("resultado");
        Location location = null;


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


        if(result!=null){
            location = result.getGeometry().getLocation();
            lat = location.getLat();
            lng = location.getLng();
            name = result.getName();
            placeId = result.getPlaceId();
            tv_details_nome.setText(name);

            if(result.getOpeningHours() != null) {
                openNow = result.getOpeningHours().getOpenNow();
                tv_details_opennow.setText(openNow.toString());
            }else{
                tv_details_opennow.setText("Unknown");
            }

            rating = result.getRating();
            if(rating != null){
                tv_details_rating.setText(Float.toString(rating));

            }else{
                tv_details_rating.setText("Unknown");
            }

        }


        cb_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_save.isChecked()){
                    Uri insertUri = PlaceContract.PlaceEntry.buildPlaceUriWithId(placeId);
                    ContentValues cv = new ContentValues();
                    cv.put(PlaceContract.PlaceEntry.COLUMN_LAT, lat);
                    cv.put(PlaceContract.PlaceEntry.COLUMN_LNG, lng);
                    cv.put(PlaceContract.PlaceEntry.COLUMN_RATING, rating);
                    cv.put(PlaceContract.PlaceEntry.COLUMN_NAME, name);
                    cv.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, placeId);
                    if(result.getPhotos()!=null && !result.getPhotos().isEmpty()){
                        cv.put(PlaceContract.PlaceEntry.COLUMN_PHOTOS, result.getPhotos().get(0).getPhotoReference());
                    }

                    ContentResolver contentResolver = getContentResolver();
                    contentResolver.insert(insertUri, cv);


                }else{
                    Uri deleteUri = PlaceContract.PlaceEntry.buildPlaceUriWithId(placeId);
                    ContentResolver contentResolver = getContentResolver();
                    contentResolver.delete(deleteUri, null, null);


                }
            }
        });

        callLoaderManager();

    }

    public void callLoaderManager(){
        getSupportLoaderManager().initLoader(ID_PLACES_LOADER, null, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng location = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(location).title("Marcador em " + name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 25));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbarpreference_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivity(intent);
                break;

            case android.R.id.home:
                finish();
                break;

            case R.id.action_deleteall:
                dialog.show();
                break;


            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id){

            case ID_PLACES_LOADER:

                Uri queryUri = PlaceContract.PlaceEntry.buildPlaceUriWithId(placeId);
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
        if(data.getCount() == 0){
           cb_save.setChecked(false);
        }else{
            cb_save.setChecked(true);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cb_save.setChecked(false);
    }
}
