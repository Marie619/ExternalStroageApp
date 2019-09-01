package com.example.getexternaldeviceapp;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {


    private static final String TAG ="Mainactivity.java" ;
    private static final String ACTION_USB_PERMISSION ="usb permission" ;
    UsbDevice device;
    UsbManager manager;
    Intent intent;
    FileSystem currentFs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(this /* Context or Activity */);

        for(UsbMassStorageDevice device: devices) {

            // before interacting with a device you need to call init()!
            try {
                device.init();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Only uses the first partition on the device
             currentFs = device.getPartitions().get(0).getFileSystem();
            Log.d(TAG, "Capacity: " + currentFs.getCapacity());
            Log.d(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
            Log.d(TAG, "Free Space: " + currentFs.getFreeSpace());
            Log.d(TAG, "Chunk size: " + currentFs.getChunkSize());

            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            manager.requestPermission(device.getUsbDevice(), permissionIntent);
        }

        UsbFile root = currentFs.getRootDirectory();

        UsbFile[] files = new UsbFile[0];
        try {
            files = root.listFiles();
            UsbFile newDir = root.createDirectory("foo");
            UsbFile file = newDir.createFile("bar.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(UsbFile file: files) {
            Log.d(TAG, file.getName());
            if(file.isDirectory()) {
                OutputStream os = new UsbFileOutputStream(file);
                try {
                    os.write("hello".getBytes());
                    os.close();
                    InputStream is = new UsbFileInputStream(file);
                    byte[] buffer = new byte[currentFs.getChunkSize()];
                    is.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }




// write to a file




// read from a file



    }

}
