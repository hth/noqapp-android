package com.noqapp.android.client.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class LocationReader {
    private TelephonyManager telephonyManager;
    private GsmCellLocation cellLocation;
    private int myLatitude, myLongitude;


    public void getLocation(Activity context) {

        //retrieve a reference to an instance of TelephonyManager
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
        final int cid = cellLocation.getCid();
        final int lac = cellLocation.getLac();
        Log.e("gsm cell id: ", String.valueOf(cid));
        Log.e("gsm location area code:", String.valueOf(lac));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Boolean result = RqsLocation(cid, lac);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result) {
                            Log.e("Location",
                                    String.valueOf((float) myLatitude / 1000000)
                                            + " : "
                                            + String.valueOf((float) myLongitude / 1000000));
                        } else {
                            Log.e("Location", "Can't find Location!");
                        }
                    }
                });
            }
        }).start();
    }

    private Boolean RqsLocation(int cid, int lac) {
        Boolean result = false;

        String urlmmap = "https://www.google.com/glm/mmap";

        try {
            URL url = new URL(urlmmap);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.connect();

            OutputStream outputStream = httpConn.getOutputStream();
            WriteData(outputStream, cid, lac);

            InputStream inputStream = httpConn.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            dataInputStream.readShort();
            dataInputStream.readByte();
            int code = dataInputStream.readInt();
            if (code == 0) {
                myLatitude = dataInputStream.readInt();
                myLongitude = dataInputStream.readInt();
                result = true;

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

    private void WriteData(OutputStream out, int cid, int lac)
            throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cid);
        dataOutputStream.writeInt(lac);

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();
    }
}
