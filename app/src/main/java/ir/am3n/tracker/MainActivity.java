package ir.am3n.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Tracker().start(this, new TrackerListener() {
            @Override
            public void onStart() {
                Log.d("Me-MainAct", "Tracker onStart()");
                Toast.makeText(MainActivity.this, "Tracker onStart()", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNeedPermissions(String[] permissions) {
                Log.d("Me-MainAct", "Tracker onNeedPermissions()");
                Toast.makeText(MainActivity.this, "Tracker onNeedPermissions()", Toast.LENGTH_LONG).show();
            }
        });

    }
}