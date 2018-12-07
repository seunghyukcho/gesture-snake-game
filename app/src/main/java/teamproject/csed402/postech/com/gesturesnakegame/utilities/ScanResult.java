package teamproject.csed402.postech.com.gesturesnakegame.utilities;

public class ScanResult {
    public double timestamp;
    public String mac_addr;
    public String uuid;
    public int major;
    public int minor;
    public double RSSI;

    public ScanResult(double timestamp_, String mac_addr_, String uuid_, int major_, int minor_, double RSSI_) {
        timestamp = timestamp_;
        mac_addr = mac_addr_;
        uuid = uuid_;
        major = major_;
        minor = minor_;
        RSSI = RSSI_;
    }
}