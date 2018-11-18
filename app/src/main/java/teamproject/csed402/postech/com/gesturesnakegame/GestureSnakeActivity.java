package teamproject.csed402.postech.com.gesturesnakegame;

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
    private static final String TAG = "WIFIScanner";

    WifiManager wifimanager;
    private int scanCount = 0;
    String result = "";

    private List<ScanResult> mScanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup WIFI
        wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Log.d(TAG, "Setup WifiManager getSystemService");

        // turn-on wifi WIFIEnabled
        if(!wifimanager.isWifiEnabled())
            wifimanager.setWifiEnabled(true);

        initWIFIScan();

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new instance of the SnakeEngine class

        //snakeEngine = new TouchSnake(this, size);
        snakeEngine = new GestureSnake(getApplicationContext(), size, getIntent());

        // Make snakeEngine the view of the Activity
        setContentView(snakeEngine);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != getPackageManager().PERMISSION_GRANTED)
            {
                Log.d("bad", "1");
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
            if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != getPackageManager().PERMISSION_GRANTED)
            {
                Log.d("bad", "2");
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
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

    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            final String action = intent.getAction();
            if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                getWIFIScanResult();
                wifimanager.startScan();
            } else if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }

    };

    public void getWIFIScanResult(){
        mScanResult = wifimanager.getScanResults();

        int sum = 0;
        int threshold_left = -50;
        int threshold_right = -50;

        // Read
        for(int i=0;i<mScanResult.size();i++){
            ScanResult result = mScanResult.get(i);
            if(result.SSID.contains("TP-LINK"))
            {
                Log.d("good:", result.SSID);
                sum += result.level;
            }
        }

        Log.d("sum: ", "" + sum);



        if(sum < threshold_left){
            ((MyApplication) this.getApplication()).setSomeVariable("left");
        }
        else if(sum > threshold_right){
            ((MyApplication) this.getApplication()).setSomeVariable("right");
        }
        else{
            ((MyApplication) this.getApplication()).setSomeVariable("forward");
        }

    }

    public void initWIFIScan(){
        scanCount = 0;
        final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        wifimanager.startScan();
        Log.d(TAG, "initWIFIScan()");
    }
}

