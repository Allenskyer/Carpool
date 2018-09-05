package com.tj.carpool.common;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class MyLocationClient {
    public LocationClient locationClient;
    public MyLocationClient(Context context)
    {
        locationClient = new LocationClient(context);
    }

    public void startLocate() {
        // 声明定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式 高精度
        option.setCoorType("bd09ll");// 设置返回定位结果是百度经纬度 默认gcj02
        option.setScanSpan(1000);// 设置发起定位请求的时间间隔 单位ms
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息
        //option.setNeedDeviceDirect(true);// 设置定位结果包含手机机头 的方向
        // 设置定位参数
        locationClient.setLocOption(option);
        locationClient.start();
    }
}
