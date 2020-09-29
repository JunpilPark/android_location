package com.test.location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager = new LocationManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                     2000);
        }

        final TextView textView = findViewById(R.id.txtView);
        Button btnGps = findViewById(R.id.btn_runGps);

        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.getCurrentLocation(getBaseContext(), new LocationManager.ResponseLocation() {
                    @Override
                    public void getCurrentLocation(Location location) {
                        if(location != null) {
                            textView.setText("현재 위치 : " + location.getLatitude() + ", " + location.getLongitude());
                        }
                        else {
                            textView.setText("location is null");
                        }
                    }
                });

            }
        });
    }

}