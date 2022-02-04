package ir.am3n.tracker;

public interface TrackerListener {

    void onStart();

    void onNeedPermissions(String[] permissions);

}
