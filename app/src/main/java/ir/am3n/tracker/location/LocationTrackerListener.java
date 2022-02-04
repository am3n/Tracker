package ir.am3n.tracker.location;

import android.location.Location;

public interface LocationTrackerListener {

    void onNeedPermissions(String[] permissions);

    void onChanged(Location location);

    void onStateChanged(GPS state);

}