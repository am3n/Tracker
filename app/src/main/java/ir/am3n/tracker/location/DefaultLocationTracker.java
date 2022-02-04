package ir.am3n.tracker.location;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

@SuppressLint({"MissingPermission", "LongLogTag"})
class DefaultLocationTracker implements LocationListener {

    private final LocationTrackerListener listener;
    private final LocationManager locationManager;
    private final long minTime;
    private final float minDistance;

    DefaultLocationTracker(Context context, long minTime, float minDistance, LocationTrackerListener listener) {
        this.listener = listener;
        this.minTime = minTime;
        this.minDistance = minDistance;
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    public void start() {
        Log.d("Me-DefaultLocationTracker", "start()");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
    }

    public void pause() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("Me-DefaultLocationTracker", "onLocationChanged()");
        listener.onChanged(location);
    }

}