package com.example.tom.apptripudacity.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double lat = -15.7801;
    private Double lng = -47.9292;
    private String name = "Brasília";
    private Boolean openNow;
    private Float rating;
    private TextView tv_details_nome;
    private TextView tv_details_opennow;
    private TextView tv_details_rating;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps_fragment);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv_details_nome = (TextView) findViewById(R.id.tv_details_nome);
        tv_details_opennow = (TextView) findViewById(R.id.tv_details_opennow);
        tv_details_rating = (TextView) findViewById(R.id.tv_details_rating);
        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("resultado");
        Result result = (Result) b.getSerializable("resultado");
        Location location = null;

        if(result!=null){
            location = result.getGeometry().getLocation();
            lat = location.getLat();
            lng = location.getLng();
            name = result.getName();
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

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


}
