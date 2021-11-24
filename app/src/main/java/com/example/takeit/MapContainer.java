package com.example.takeit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.transition.MaterialContainerTransform;

import java.util.ArrayList;
import java.util.List;

public class MapContainer extends AppCompatActivity implements OnMapReadyCallback {


    private List<Marker> markers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        setContentView(R.layout.activity_map_container);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //latslngs = extras.getArra("places");

            //retriee from database

            //intent retrieve
            Intent intent= getIntent();
            ArrayList<String> test = intent.getStringArrayListExtra("test");
            Log.d("contents", test.toString());
        }

    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("mapped", "openmap");
        CordRoomDatabase db = CordRoomDatabase.getDatabase(getApplicationContext());
        CordDao mCordDao = db.cordDao();
        List<Coordins>  listofcoords = mCordDao.getAll();
        for(int i = 0; i < listofcoords.size(); i++) {
        //for (Coordins cord: listofcoords){

            Coordins cord = listofcoords.get(i);
            LatLng place = new LatLng(cord.lat, cord.lng);

            Marker m = googleMap.addMarker(new MarkerOptions().position(place).title("place"));
//            Marker m = mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(lat, lon))
//                    .title(marker_title));
            markers.add(m);

        }

        //To do this, first calculate the bounds of all the markers like so:

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();


        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
       // Finally move the map:

        googleMap.moveCamera(cu);
       // Or if you want an animation:

        googleMap.animateCamera(cu);

    }


}