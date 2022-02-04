package ir.am3n.tracker.location;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

class FusedLocationTracker {

    private final FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private final LocationCallback locationCallback;

    FusedLocationTracker(Context context, long interval, long fastestInterval, int priority, LocationTrackerListener listener) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        createLocationRequest(interval, fastestInterval, priority);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    listener.onChanged(location);
                }
            }
        };
    }

    private void createLocationRequest(long interval, long fastestInterval, int priority) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        locationRequest.setPriority(priority);
    }

    @SuppressLint("MissingPermission")
    public void start() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void pause() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}