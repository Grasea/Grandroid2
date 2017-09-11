package com.grasea.grandroid.ble.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.ParcelUuid;

import com.grasea.grandroid.ble.Config;
import com.grasea.grandroid.ble.GrandroidBle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Alan Ding on 2016/5/16.
 */
public class DeviceScanner extends BaseScanner {

    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<UUID> uuids;
    private ArrayList<String> names;
    private boolean retry = false;
    private int retryCount = 0;
    private BluetoothAdapter.LeScanCallback leScanCallback;

    private ScanCallback scanCallback;

    private ScanResultHandler scanResultHandler;
    private boolean scanTaskDone = false;
    private int scanMode = ScanSettings.SCAN_MODE_BALANCED;

    public DeviceScanner() {
        uuids = new ArrayList<>();
        names = new ArrayList<>();
    }

    //TODO 修改retry機制
    public DeviceScanner setRetry(int count) {
        this.retry = count > 0;
        this.retryCount = count;
        return this;
    }

    public DeviceScanner filterUUID(String uuid) {
        UUID formatUUID = UUID.fromString(uuid);
        if (!uuids.contains(formatUUID)) {
            uuids.add(formatUUID);
        }
        return this;
    }

    public DeviceScanner filterUUIDs(String[] uuids) {
        for (String uuid : uuids) {
            UUID formatUUID = UUID.fromString(uuid);
            if (!this.uuids.contains(formatUUID)) {
                this.uuids.add(UUID.fromString(uuid));
            }
        }
        return this;
    }

    public DeviceScanner unfilterUUID(String uuid) {
        uuids.remove(UUID.fromString(uuid));
        return this;
    }

    public DeviceScanner unfilterUUIDs(String[] uuids) {
        for (String uuid : uuids) {
            this.uuids.remove(UUID.fromString(uuid));
        }
        return this;
    }

    public DeviceScanner filterName(String name) {
        if (!names.contains(name)) {
            names.add(name);
        }
        return this;
    }

    public DeviceScanner filterNames(String[] names) {
        for (String name : names) {
            if (!this.names.contains(name)) {
                this.names.add(name);
            }
        }
        return this;
    }

    public DeviceScanner setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
        return this;
    }

    @Override
    public DeviceScanner setScanResultHandler(ScanResultHandler scanResultHandler) {
        this.scanResultHandler = scanResultHandler;
        return this;
    }

    @Override
    public ScanResultHandler getScanResultHandler() {
        return scanResultHandler;
    }

    @Override
    public long getTimeout() {
        return getScanResultHandler() != null ? getScanResultHandler().getTimeout() : BaseScanResultHandler.DEFAULT_TIMEOUT;
    }

    @Override
    public boolean isScanning() {
        return scanning;
    }

    @Override
    public void startScan() {
        Config.logi("start to Scan:" + isScanning() + ", bluetoothAdapter ready?" + (bluetoothAdapter != null));
        if (!isScanning() && bluetoothAdapter != null) {
            scanTaskDone = false;
            this.leScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (uuids.isEmpty()) {
                        //Defult scan.
                        Config.logi("names length:" + names.size() + ", device is null?" + (device == null) + ", deviceName:" + device.getName());
                        if (names != null && !names.isEmpty()) {
                            for (String name : names) {
                                if (device.getName() != null && name.contains(device.getName())) {
                                    onDeviceFind(device, rssi, scanRecord);
                                    break;
                                }
                            }
                        } else {
                            onDeviceFind(device, rssi, scanRecord);
                        }
                    } else {
                        //Scan by UUIDs.
                        onDeviceFind(device, rssi, scanRecord);
                    }
                }
            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && bluetoothAdapter.isOffloadedFilteringSupported() && bluetoothAdapter.isOffloadedScanBatchingSupported()) {
                scanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        BluetoothDevice device = null;
                        int rssi = 0;
                        byte[] scanRecord = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            device = result.getDevice();
                            rssi = result.getRssi();
                            try {
                                scanRecord = result.getScanRecord().getBytes();
                            } catch (NullPointerException e) {
                                Config.loge(e);
                            }
                        }
                        //Defult scan.
                        Config.logi("names length:" + names.size() + ", device is null?" + (device == null) + ", deviceName:" + device.getName());
                        if (names != null && !names.isEmpty()) {
                            for (String name : names) {
                                if (device.getName() != null && name.contains(device.getName())) {
                                    onDeviceFind(device, rssi, scanRecord);
                                    break;
                                }
                            }
                        } else {
                            onDeviceFind(device, rssi, scanRecord);
                        }

                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        Config.logi("onBatchScanResults");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            for (ScanResult scanResult : results) {
                                Config.logi("Device Found:( " + scanResult.getRssi() + ")" + scanResult.getDevice().getName() + "[" + scanResult.getDevice().getAddress() + "]");
                                if (results.indexOf(scanResult) == results.size() - 1) {
                                    scanTaskDone = true;
                                }
                                onDeviceFind(scanResult.getDevice(), scanResult.getRssi(), scanResult.getScanRecord().getBytes());
                            }
                        }
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        onDeviceFailed(errorCode);
                    }
                };
                if (uuids.isEmpty()) {
                    if (names.isEmpty()) {
                        bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
                        Config.logi("[StartScan]");
                    } else {
                        ArrayList<ScanFilter> scanFilters = new ArrayList<>();
                        for (String name : names) {
                            scanFilters.add(new ScanFilter.Builder().setDeviceName(name).build());
                        }
                        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(scanMode).setReportDelay(2000).build();
                        bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, scanCallback);
                        Config.logi("[StartScan]Has need watch names");
                        Config.logi(Arrays.toString(names.toArray(new String[]{})));

                    }
                } else {
                    ArrayList<ScanFilter> scanFilters = new ArrayList<>();
                    for (UUID uuid : uuids) {
                        scanFilters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(uuid)).build());
                    }
                    ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(scanMode).setReportDelay(2000).build();
                    bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, scanCallback);
                    Config.logi("[StartScan]Has need watch uuids");
                }
            } else {
                if (uuids.isEmpty()) {
                    bluetoothAdapter.startLeScan(this.leScanCallback);
                } else {
                    bluetoothAdapter.startLeScan(uuids.toArray(new UUID[]{}), this.leScanCallback);
                }
            }
            startTimer();
        }
    }

    @Override
    public void stopScan() {
        if (isScanning() && bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (scanCallback != null) {
                    try {
                        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                    } catch (Exception e) {
                        Config.loge(e);
                    }
                }
            } else {
                try {
                    bluetoothAdapter.stopLeScan(leScanCallback);
                } catch (Exception e) {
                    Config.loge(e);
                }
            }
            stopTimer();
        }
    }

    @Override
    public void onDeviceFind(BluetoothDevice device, int rssi, byte[] scanRecord) {
//        GrandroidBle.getInstance().addDevice(device);
        if (scanResultHandler != null) {
            scanResultHandler.onDeviceFind(device, rssi, scanRecord);
        }
        if (scanTaskDone) {
            scanTaskDone = false;
            stopScan();
        }
    }

    /**
     * Work after API 21.
     *
     * @param errorCode
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDeviceFailed(int errorCode) {
        if (scanResultHandler != null) {
            scanResultHandler.onDeviceFailed(errorCode);
        }
    }

    public DeviceScanner setScanMode(int scanMode) {
        this.scanMode = scanMode;
        return this;
    }

    private void startTimer() {
        Config.logi("Timer turn on");
        scanning = true;
        timer = new Timer("ScannerTimer");
        try {
            timer.schedule(createTimerTask(), 0, 1000);
        } catch (Exception e) {
            Config.loge(e);
        }

    }

    private void stopTimer() {
        Config.logi("Timer turn off");
        scanning = false;
        if (timer != null) {
            timer.cancel();
        }
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            private long startTime = 0;
            private int timeCount = 0;

            @Override
            public void run() {
                if (startTime == 0) {
                    startTime = System.currentTimeMillis();
                    return;
                } else {
                    long time = System.currentTimeMillis() - startTime;
//                    Config.logi("pass time:" + time);
                    if (System.currentTimeMillis() - startTime >= getTimeout()) {
                        timeCount++;

                        if (scanResultHandler != null) {
                            scanResultHandler.onTimeout(DeviceScanner.this);
                        }
                        if (retry && retryCount - timeCount >= 0) {
                            startTime = 0;
                            return;
                        } else {
                            //似乎有問題，先移除
//                            stopScan();
                        }
                    }
                }
            }
        };
    }

}
