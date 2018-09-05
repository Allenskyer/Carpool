package com.tj.carpool.common;

import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;

import static android.content.ContentValues.TAG;

public class MyLocationListener extends BDAbstractLocationListener {

    private  BDLocation location;

    public boolean isLocationSet() {
        return locationSet;
    }

    boolean locationSet = false;
    @Override
    public void onReceiveLocation(BDLocation location) {
        // 非空判断
        if (location != null) {
            this.location = location;
            locationSet = true;
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String address = location.getAddrStr();
            Log.i(TAG, "address:" + address + " latitude:" + latitude
                    + " longitude:" + longitude + "---");
        }
    }
    public BDLocation getLocation() {
        return location;
    }

}