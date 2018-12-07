package teamproject.csed402.postech.com.gesturesnakegame.utilities;

import java.util.ArrayList;

public class ScanResultList extends ArrayList<ScanResult> {
    public void add_entry(double timestamp, String mac_addr, String uuid, int major, int minor, double RSSI) {
        super.add(new ScanResult(timestamp, mac_addr, uuid, major, minor, RSSI));
    }

    public ScanResultList() {

    }
    public ScanResultList(ScanResultList target) {
        super(target);
    }
}