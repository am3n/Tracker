package ir.am3n.tracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class Tracker {

    static int NOTIFICATION_ID = 1996;
    static String NOTIFICATION_CHANNEL_ID = "am3n_tracker";

    public void start(Context context, TrackerListener listener) {
        int accessFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int accessCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessBackgroundLocation = PackageManager.PERMISSION_GRANTED;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            accessBackgroundLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        boolean granted = accessFineLocation == PackageManager.PERMISSION_GRANTED
                && accessCoarseLocation == PackageManager.PERMISSION_GRANTED
                && accessBackgroundLocation == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            ContextCompat.startForegroundService(context, new Intent(context, TrackerService.class));
            listener.onStart();
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

}