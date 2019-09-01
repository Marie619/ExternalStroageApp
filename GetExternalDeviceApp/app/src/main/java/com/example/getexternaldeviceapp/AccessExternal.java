package com.example.getexternaldeviceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.snatik.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class AccessExternal extends AppCompatActivity {

    private static final String TAG = "AccessExternal";
    private static final String ACTION_USB_PERMISSION =
            "com.example.getexternaldeviceapp.USB_PERMISSION";

    UsbMassStorageDevice[] devices;
    UsbManager usbManager;
    PendingIntent permissionIntent;
    UsbDevice usbDevice;
    BroadcastReceiver usbReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_external);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
//            Toast.makeText(this, ""+device, Toast.LENGTH_SHORT).show();
            //your code
        }

        permissionIntent = PendingIntent.getBroadcast(AccessExternal.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);

//        Intent intent = getIntent();
//        usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        registerReceiver(usbReceiver, filter);
//        usbManager.requestPermission(usbDevice, permissionIntent);


// Normal route

//        try {
//            massStorage();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        usbReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                        usbManager.requestPermission(device, permissionIntent);


                        if (usbManager.hasPermission(device)) {
                            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "not granted", Toast.LENGTH_SHORT).show();
                        }


                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null) {
                                //call method to set up device communication
                                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "permission denied for device " + device);
                        }
                    }
                }
            }
        };


        //  accessStorage();

    }

    private void accessStorage() {

        Storage storage = new Storage(getApplicationContext());

// get external storage
        String path = storage.getExternalStorageDirectory();

// new dir
        String newDir = path + File.separator + "Sample Directory";
        storage.createDirectory(newDir);

    }

    private void massStorage() throws IOException {

        devices = UsbMassStorageDevice.getMassStorageDevices(this /* Context or Activity */);


        for (UsbMassStorageDevice device : devices) {

            // before interacting with a device you need to call init()!
            device.init();

            //Calling the pending intent

            // Only uses the first partition on the device
            FileSystem currentFs = device.getPartitions().get(0).getFileSystem();

            Toast.makeText(this, "" + currentFs, Toast.LENGTH_SHORT).show();

//            Toast.makeText(this, "" + currentFs, Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "Capacity: " + currentFs.getCapacity());
//            Log.d(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
//            Log.d(TAG, "Free Space: " + currentFs.getFreeSpace());
//            Log.d(TAG, "Chunk size: " + currentFs.getChunkSize());


        }

    }


}
