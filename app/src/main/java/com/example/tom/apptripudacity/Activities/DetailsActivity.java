package com.example.tom.apptripudacity.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.tom.apptripudacity.Models.Geometry;
import com.example.tom.apptripudacity.Models.Location;
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

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double lat = -15.7801;
    private Double lng = -47.9292;
    private String name = "Brasília";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);



        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("resultado");
        Result result = (Result) b.getSerializable("resultado");
        Location location = null;

        if(result!=null){
            location = result.getGeometry().getLocation();
            lat = location.getLat();
            lng = location.getLng();
            name = result.getName();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng location = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(location).title("Marcador em " + name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 25));

    }
}
