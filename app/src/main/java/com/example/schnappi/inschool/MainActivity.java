package com.example.schnappi.inschool;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity /*implements OnCompleteListener<Void>*/ {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final String TAG = "MainActivity";

    private GeofencingClient mGeofencingClient;

    private Geofence geo;

    private PendingIntent mGeofencePendingIntent;

    private Button addGeofenceButton;
    private Button removeGeofenceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        createGeoFence();

        mGeofencePendingIntent = null;


        mGeofencingClient = LocationServices.getGeofencingClient(this);

        fuckingsLydlosPermission();


        addGeofenceButton = findViewById(R.id.activateButton);
        removeGeofenceButton = findViewById(R.id.deactivateButton);
        removeGeofenceButton.setEnabled(false);
        // activate the geofence
        addGeofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGeofences();
                removeGeofenceButton.setEnabled(true);
                addGeofenceButton.setEnabled(false);
            }
        });

        // deactivate the geofence
        removeGeofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeGeofence();

                skruPåLyd();
                //skrur av removeButtonm
                removeGeofenceButton.setEnabled(false);
                addGeofenceButton.setEnabled(true);
            }
        });
    }


    // lager geofence
    public void createGeoFence()
    {
        geo = new Geofence.Builder()
                .setRequestId("hib")
                .setCircularRegion(Values.lengdegrad, Values.breddegrad, Values.radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        Log.i(TAG, "createGeoFence");
    }

    private GeofencingRequest getGeofencingRequest()
    {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geo);

        Log.i(TAG, "getGeofencingRequest");
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent()
    {
        if(mGeofencePendingIntent != null)
            return mGeofencePendingIntent;

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent
                .getBroadcast(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, "getGeofencePendingIntent");
        return mGeofencePendingIntent;
    }



    @SuppressWarnings("MissingPermission")
    private void addGeofences()
    {
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())

                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "addGeofence: success!");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "addGeofence: FAIL!");
                    }
                });
    }


    private void removeGeofence()
    {
        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       Log.i(TAG, "removeGeofence: successe");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "removeGeofence: fail");
                    }
                });
    }

    private void fuckingsLydlosPermission(){
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
    }

    private void skruPåLyd(){

        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Toast toast = Toast.makeText(this, "Unmute", Toast.LENGTH_SHORT);
        toast.show();

    }
}