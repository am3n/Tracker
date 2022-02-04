package ir.am3n.tracker.location;

interface GPSListener {

    void onStateChanged(int gps, int network);

}