package ir.am3n.tracker.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;

public class BaseLocationTracker {

    private final Context context;
    private final LocationTrackerListener listener;
    private final GPSReceiver gpsReceiver;
    private FusedLocationTracker fusedLocationTracker;
    private DefaultLocationTracker defaultLocationTracker;
    private GPS state = GPS.UNKNOWN;

    public BaseLocationTracker(Context context, LocationTrackerListener listener) {
        this.context = context;
        this.listener = listener;
        if (isGooglePlayServicesAvailable(context)) {
            fusedLocationTracker = new FusedLocationTracker(
                    context,
                    5 * 1000,
                    5 * 1000,
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    listener
            );
        } else {
            defaultLocationTracker = new DefaultLocationTracker(
                    context,
                    5 * 1000,
                    20f,
                    listener
            );
        }
        gpsReceiver = new GPSReceiver(context, (GPSListener) (gps, network) -> {
            if (gps == 1 && state != GPS.ON) {
                state = GPS.ON;
                listener.onStateChanged(state);
                start();
            } else if (gps == 0 && state != GPS.OFF) {
                state = GPS.OFF;
                listener.onStateChanged(state);
            }
        });
        context.registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        new Handler(Looper.getMainLooper()).post(() -> gpsReceiver.onReceive(context, new Intent(LocationManager.PROVIDERS_CHANGED_ACTION)));
    }

    private boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        Log.d("Me-BaseLocationTracker", "isGooglePlayServicesAvailable " + resultCode);
        return resultCode == ConnectionResult.SUCCESS;
    }

    @SuppressLint("MissingPermission")
    public void start() {

        if (permissionsGranted()) {

            Location location = gpsReceiver.getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null)
                listener.onChanged(location);

            if (fusedLocationTracker != null)
                fusedLocationTracker.start();
            if (defaultLocationTracker != null)
                defaultLocationTracker.start();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                listener.onNeedPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                });
            } else {
                listener.onNeedPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
        }

    }

    public void pause() {
        if (fusedLocationTracker != null)
            fusedLocationTracker.pause();
        if (defaultLocationTracker != null)
            defaultLocationTracker.pause();
    }

    public void stop() {
        pause();
        if (gpsReceiver != null)
            context.unregisterReceiver(gpsReceiver);
    }

    private boolean permissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int fineLocation = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int coarseLocation = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessBackgroundLocation = PackageManager.PERMISSION_GRANTED;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                accessBackgroundLocation = context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
            ArrayList<String> permissions = new ArrayList<>();
            if (fineLocation != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && accessBackgroundLocation != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
            return permissions.isEmpty();

        } else {
            return true;
        }
    }

}