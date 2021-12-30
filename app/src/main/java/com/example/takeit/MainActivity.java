package com.example.takeit;


//todo longclick on mapit opens context menu , what happens next


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    int LOCATION_REQUEST_CODE = 10001;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    TextView title;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for(Location location: locationResult.getLocations()) {
                Log.d(TAG, "onLocationResult: " + location.toString());
                //Log.d("going in", String.valueOf(location.getLatitude()));
            }
        }
    };


    final int REQUEST_CODE_FINE_LOCATION = 1234;

    // private FusedLocationProviderClient fusedLocationProviderClient;
    Button camera_button;
    Button takeIT_button;
    Button gotoMapbutton;
    ArrayList places;
    Double Latitude;
    Double Longitude;

    //private WordModel mWordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        title = findViewById(R.id.textView);
        takeIT_button = findViewById(R.id.button);
        gotoMapbutton = findViewById(R.id.goToMap);
        camera_button = findViewById(R.id.button2);

        registerForContextMenu(takeIT_button);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("start camera","start camera");
                Intent startCamera = new Intent(MainActivity.this, MyCamera.class);
                //Intent takePicture =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(startCamera);
            }
        });



//        takeIT_button.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View v) {
//                Log.d("longpress","longpress");
//                Intent startCamera = new Intent(MainActivity.this, MyCamera.class);
//                //Intent takePicture =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivity(startCamera);
//                //startActivityForResult(takePicture);
//                return false;
//            }
//        });

        takeIT_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlace();
            }
        });
        gotoMapbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });

        places = new ArrayList<>();

    }


    //long click on mapit button
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("MapIt or Take photo");
        getMenuInflater().inflate(R.menu.example_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.option_1:
                savePlace();
                return true;
            case R.id.option_2:
                Intent startCamera = new Intent(MainActivity.this, MyCamera.class);
                //Intent takePicture =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(startCamera);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("permission granted", "1");
            checkSettingsAndStartLocationUpdates();

        } else {
            Log.d("2","2");
            askLocationPermission();
        }
    }

    //top right menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }


    private void savePlace(){

        //getLastLocation();
        FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        Log.d("4","4");

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d("5","5");
                if (location != null) {
                    //We have a location
                    //add to database
                    //adding to database
                    CordRoomDatabase db = CordRoomDatabase.getDatabase(getApplicationContext());
                    CordDao mCordDao = db.cordDao();
                    Log.d(TAG, "getlong: " + location.getLongitude());
                    mCordDao.insertAll(new Coordins(location.getLatitude(), location.getLongitude()));
                    //stopLocationUpdates();
                }
            }
        });

    }

    private void showMap(){

        Intent startmap = new Intent(this, MapContainer.class);
        startmap.putIntegerArrayListExtra("test", (ArrayList<Integer>) places);
        //startmap.putExtra("places", places);
        //display Map
        startActivity(startmap);

    }

    /////////////
    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Settings of device are satisfied and we can start location updates
                startLocationUpdates();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MainActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }



    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // getLastLocation();
                //askLocationPermission();
                checkSettingsAndStartLocationUpdates();
            } else {
                //Permission not granted
            }
        }
    }
}