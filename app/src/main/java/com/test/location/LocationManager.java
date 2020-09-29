package com.test.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

class LocationManager {

    interface ResponseLocation {
        void getCurrentLocation(Location location);
    }

    public void getCurrentLocation(Context context, final ResponseLocation responseLocation) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("Access location permission denied");
        }
        @SuppressLint("RestrictedApi") Executor executor = ArchTaskExecutor.getIOThreadExecutor();
        final FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(executor, new OnCompleteListener<Location>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.getResult() != null) {
                    Log.d("jppark", "addOnCompleteListener : current Thread - " + Thread.currentThread());
                    runResponseLocationMainThread(responseLocation, task.getResult());
                }
                else {
                    requestCurrentLocation(fusedLocationProviderClient, responseLocation);
                }
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void runResponseLocationMainThread(final ResponseLocation responseLocation, final Location location) {
        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                responseLocation.getCurrentLocation(location);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestCurrentLocation(final FusedLocationProviderClient fusedLocationProviderClient, final ResponseLocation responseLocation) {
        Log.i("jppark", "[LocationManager - requestCurrentLocation]: ");
        Log.d("jppark", "requestCurrentLocation : current Thread - " + Thread.currentThread());
        Log.d("jppark", "requestCurrentLocation : Looper- " + Looper.getMainLooper());

        final LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("jppark", "requestCurrentLocation - onLocationResult : current Thread - " + Thread.currentThread());
                runResponseLocationMainThread(responseLocation, locationResult.getLastLocation());
                fusedLocationProviderClient.removeLocationUpdates(this);
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(createLocationRequest() ,callback, Looper.getMainLooper()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("jppark", "[LocationManager -requestCurrentLocation - onComplete]: " + task.getException());
            }
        });
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

}
