package com.example.schnappi.inschool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        private GeofencingClient mGeofencingClient;

        mGeofencingClient = LocationServices.getGeofencingClient(this);


    }
}
