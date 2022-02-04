package ir.am3n.tracker.location;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;

class GPSReceiver extends BroadcastReceiver {

    private final GPSListener listener;
    private final LocationManager locationManager;

    GPSReceiver(Context context, GPSListener listener) {
        this.listener = listener;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        context.getSharedPreferences("Am3nTracker", MODE_PRIVATE)
                .edit()
                .putInt("lastGPSState", -1)
                .putInt("lastNetworkState", -1)
                .apply();
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            try {
                int gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? 1 : 0;
                int network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? 1 : 0;
                SharedPreferences sh = context.getSharedPreferences("Am3nTracker", MODE_PRIVATE);
                int lastGPSState = sh.getInt("lastGPSState", -1);
                int lastNetworkState = sh.getInt("lastNetworkState", -1);
                if (lastGPSState != gps || lastNetworkState != network) {
                    lastGPSState = gps;
                    lastNetworkState = network;
                    sh.edit().putInt("lastGPSState", lastGPSState).putInt("lastNetworkState", lastNetworkState).apply();
                    listener.onStateChanged(lastGPSState, lastNetworkState);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}