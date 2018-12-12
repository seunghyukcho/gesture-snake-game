package teamproject.csed402.postech.com.gesturesnakegame;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Scanner;

import teamproject.csed402.postech.com.gesturesnakegame.engines.GestureSnake;

public class GestureSnakeActivity extends AppCompatActivity {
    // Declare an insance of SnakeEngine
    GestureSnake snakeEngine;

    private List<ScanResult> mScanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new instance of the SnakeEngine class

        //snakeEngine = new TouchSnake(this, size);
        snakeEngine = new GestureSnake(getApplicationContext(), size);

        // Make snakeEngine the view of the Activity
        setContentView(snakeEngine);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != getPackageManager().PERMISSION_GRANTED) {
                Log.d("bad", "1");
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != getPackageManager().PERMISSION_GRANTED) {
                Log.d("bad", "2");
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
            if (checkSelfPermission(Manifest.permission.BLUETOOTH) != getPackageManager().PERMISSION_GRANTED) {
                Log.d("bad", "2");
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, 256);
            }
        }

        snakeEngine.resume();

    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        snakeEngine.pause();
    }
}

