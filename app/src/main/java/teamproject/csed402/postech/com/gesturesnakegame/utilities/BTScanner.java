package teamproject.csed402.postech.com.gesturesnakegame.utilities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BTScanner {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;

    private int SCAN_PERIOD = 100000000; // default, 1 second
    // requesting scan more than LIMIT_NUMOFSCAN within LIMIT_PERIOD will block scanning
    private final int LIMIT_PERIOD = 30;
    private final int LIMIT_NUMOFSCAN = 10;

    private ScanResultList scanResults;
    private long startTime = 0; // start time of scan request (updated only when the scan is expired; including first try)

    private Handler mHandler;
    private boolean mScanning; // scanning result is returned (externally)
    private boolean mExpired = true; // actual scanning is performed (internally)

    private boolean leftGesture = false;
    private boolean rightGesture = false;

    public BTScanner(Context ctx)
    {
        mHandler = new Handler();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (!ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // no support of Bluetooth LE
        } else if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ctx.startActivity(enableBtIntent);
        }

        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<ScanFilter>();
    }

    public boolean getLeftGesture() { return leftGesture; }
    public void setLeftGesture(boolean input) { leftGesture = input; }
    public boolean getRightGesture() { return rightGesture; }
    public void setRightGesture(boolean input) { rightGesture = input; }

    public void request() {
        request(SCAN_PERIOD);
    }

    public void request(int period) {
        scanResults = new ScanResultList();
        startTime = System.currentTimeMillis();

        scanLeDevice(period);
    }

    // for one-time result return
    public ScanResultList returnAllResult()
    {
        if (scanResults != null)
            return scanResults;
        else
            return null;
    }

    private void scanLeDevice(long period) {
        // Stops scanning after a pre-defined scan period.

        long period_ = 0;
        long current = System.currentTimeMillis();
        long limitTimePerPeriod = LIMIT_PERIOD / LIMIT_NUMOFSCAN * 1000;

        // if it's not idle, adjust the period of stopScan
        if (!mExpired)
        {
            // https://stackoverflow.com/questions/4378533/cancelling-a-handler-postdelayed-process
            mHandler.removeCallbacksAndMessages(null);

            // if stopScan is going to executed before the period, extend the period
            if (startTime + LIMIT_PERIOD / LIMIT_NUMOFSCAN * 1000 < current + period)
                period_ = current + period - startTime - limitTimePerPeriod;
            else // otherwise, just let stopScan execute in original time
                period_ = startTime + limitTimePerPeriod - current;
        }
        else { // if it's idle, let stopScan run at least LIMIT_PERIOD / LIMIT_NUMOFSCAN * 1000 ms
            period_ = Math.max(period, LIMIT_PERIOD / LIMIT_NUMOFSCAN * 1000);
        }

        // real stopScan
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mExpired = true;
                mLEScanner.stopScan(mScanCallback);
            }
        }, period_);

        // just stopping scanResult result
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
            }
        }, period);

        mScanning = true;

        // if scanning is not initiated, initiate scan (startScan)
        if (mExpired) {
            mExpired = false;
            mLEScanner.startScan(filters, settings, mScanCallback);
        }
    }

    public void stopScan()
    {
        // don't actually stop scanning of Bluetooth LE, it will be handled by the mHandler
        mScanning = false;
    }

    public void waitForScanning() {
        while (mScanning) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return;
    }

    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // Device scan callback
    private ScanCallback mScanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result)
        {
            if (mScanning) { // if it's on but scan period is expired, ignore everything
                super.onScanResult(callbackType, result);
                byte[] scanRecord = result.getScanRecord().getBytes();

                int startByte = 2;
                boolean patternFound = false;
                while (startByte <= 5) {
                    if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && // Identifies an iBeacon
                            ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { // Identifies correct data length
                        patternFound = true;
                        break;
                    }
                    startByte++;
                }

                if (patternFound) {
                    byte[] uuidBytes = new byte[16];
                    System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                    String hexString = bytesToHex(uuidBytes);

                    // Here is your UUID
                    String uuid = hexString.substring(0, 8) + "-" +
                            hexString.substring(8, 12) + "-" +
                            hexString.substring(12, 16) + "-" +
                            hexString.substring(16, 20) + "-" +
                            hexString.substring(20, 32);

                    int major = (scanRecord[25] & 0xff) * 0x100 + (scanRecord[26] & 0xff);
                    int minor = (scanRecord[27] & 0xff) * 0x100 + (scanRecord[28] & 0xff);

                    double timestamp = (System.currentTimeMillis() - startTime) / 1000.0;

                    // timestamp, mac_addr, uuid, major, minor, RSSI
                    if(minor == 1) {
                        scanResults.add_entry(timestamp, result.getDevice().getAddress(), uuid, major, minor, result.getRssi());
                        Log.d("gesture_value", Double.toString(result.getRssi()));
                        checkGesture();
                    }
                }
            }
        }

        public void checkGesture() {
            if(leftGesture || rightGesture) return;

            NeuralNet model = new NeuralNet();
            ArrayList<ScanResult> results = returnAllResult();

            double[] input = new double[results.size()];
            for(int i = 0; i < results.size(); i++)
                input[i] = results.get(i).RSSI;

            int gesture = model.run(input);

            Log.d("gesture_check", "length: " + Integer.toString(results.size()) + " result: " + Integer.toString(gesture));

            if(gesture == 2) {
                setLeftGesture(true);
                scanResults = new ScanResultList();
            }
            else if(gesture == 1) {
                setRightGesture(true);
                scanResults = new ScanResultList();
            }
        }

        @Override
        public void onScanFailed(int errorCode)
        {
            super.onScanFailed(errorCode);
            Log.i("onScanFailed", "|" + "2222222222222" + "|" + errorCode);
        }

        @Override
        public void onBatchScanResults(List<android.bluetooth.le.ScanResult> results)
        {
            super.onBatchScanResults(results);
            for (android.bluetooth.le.ScanResult result : results)
            {
                Log.i("onBatchScanResults", "33333333333333|" + result.getDevice().getName() + "|" + result.getDevice().getAddress() + "|");
            }
        }
    };
}
