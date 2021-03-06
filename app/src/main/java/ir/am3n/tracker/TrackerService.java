package ir.am3n.tracker;

import static ir.am3n.tracker.Tracker.NOTIFICATION_CHANNEL_ID;
import static ir.am3n.tracker.Tracker.NOTIFICATION_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Arrays;

import ir.am3n.tracker.location.BaseLocationTracker;
import ir.am3n.tracker.location.GPS;
import ir.am3n.tracker.location.LocationTrackerListener;

public class TrackerService extends Service implements LocationTrackerListener, SensorEventListener {

    private static final int RQST_OPEN = 1000;
    private static final int RQST_EXIT = 1001;

    private static final String ACTION_EXIT = "TRACKER_ACTION_EXIT";

    private BaseLocationTracker locationTracker;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Me-TrackerService", "onCreate()");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroupSummary(true)
                .setAutoCancel(false)
                .setLocalOnly(false)
                .setContentText("is tracking your location")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_location);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setDescription(NOTIFICATION_CHANNEL_ID);
            notificationChannel.setSound(null, null);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        Intent intentOpenApp = new Intent(this, MainActivity.class);
        intentOpenApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        builder.setContentIntent(
                PendingIntent.getActivity(this, RQST_OPEN, intentOpenApp,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0)
        );


        Intent intentExit = new Intent(this, TrackerService.class);
        intentExit.setAction(ACTION_EXIT);
        builder.addAction(
                new NotificationCompat.Action(0, "Stop",
                        PendingIntent.getService(this, RQST_EXIT, intentExit,
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0)
                )
        );

        startForeground(NOTIFICATION_ID, builder.build());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Me-TrackerService", "onStartCommand()");
        if (ACTION_EXIT.equals(intent.getAction())) {
            if (locationTracker != null)
                locationTracker.stop();
            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.unregisterListener(this);
            stopForeground(true);
            stopSelf();

        } else {

            if (locationTracker != null)
                locationTracker.stop();
            locationTracker = new BaseLocationTracker(this, this);
            locationTracker.start();

            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.unregisterListener(this);
            Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (gyroscopeSensor == null) {
                Log.d("Me-MainAct", "Gyroscope sensor not available");
                Toast.makeText(this, "Gyroscope sensor not available", Toast.LENGTH_LONG).show();
            } else {
                sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometerSensor == null) {
                Log.d("Me-MainAct", "Accelerometer sensor not available");
                Toast.makeText(this, "Accelerometer sensor not available", Toast.LENGTH_LONG).show();
            } else {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

        }
        return START_STICKY;
    }


    @Override
    public void onNeedPermissions(String[] permissions) {
        Log.d("Me-TrackerService", "onNeedPermissions()");
    }

    @Override
    public void onChanged(Location location) {
        Log.d("Me-TrackerService", "onChanged() lat:" + location.getLatitude() + " lng:" + location.getLongitude());
        Toast.makeText(this, "lat:" + location.getLatitude() + " lng:" + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStateChanged(GPS state) {
        Log.d("Me-TrackerService", "onStateChanged() " + state.toString());
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("Me-MainAct", "onSensorChanged  " + sensorEvent.sensor.getName() + "  " + Arrays.toString(sensorEvent.values) + "  " + sensorEvent.accuracy);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d("Me-MainAct", "onAccuracyChanged  " + sensor.getName() + "  " + i);
    }

}
