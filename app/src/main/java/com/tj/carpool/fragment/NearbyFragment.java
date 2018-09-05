package com.tj.carpool.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tj.carpool.CreateCarpoolActivity;
import com.tj.carpool.R;
import com.tj.carpool.common.HttpHelper;
import com.tj.carpool.datastructure.CarpoolActivity;
import com.tj.carpool.datastructure.Location;
import com.tj.carpool.datastructure.Passenger;


import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateCarpoolFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateCarpoolFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public LocationClient locationClient = null;
    private MyLocationListener myListener = null;
    private List<CarpoolActivity> activityList = new ArrayList<CarpoolActivity>();
    ListView listView;
    String tempString;
    GeoCoder mSearch = GeoCoder.newInstance();
    boolean isReady =false;

    public class ActivityAdapter extends ArrayAdapter<CarpoolActivity> {


        public ActivityAdapter(Context context, int textViewResourceId, List<CarpoolActivity> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            CarpoolActivity carpoolActivity = getItem(position); // 获取当前项的Dtable 实

            if(convertView == null){

                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item, null);
            }
            TextView textView=(TextView) convertView.findViewById(R.id.activity_list_item);

            LatLng oriPoint = new LatLng(carpoolActivity.getDepartureLoc().getLatitude(),carpoolActivity.getDepartureLoc().getLongitude());
            LatLng destPoint = new LatLng(carpoolActivity.getTargetLoc().getLatitude(),carpoolActivity.getTargetLoc().getLongitude());

            //获取起点地名
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(oriPoint));
            String oriAddress = tempString;
            //获取终点地名
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(destPoint));
            String destAddress = tempString;

            textView.setText(
                    "起点："+oriAddress+"\n"+
                            "终点："+destAddress+"\n"+
                            "发车时间"+new Timestamp(carpoolActivity.getDepartureTime()).toString()
            );


            return convertView;
        }

    }

    public NearbyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateCarpoolFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearbyFragment newInstance() {
        NearbyFragment fragment = new NearbyFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);
        listView =(ListView) view.findViewById(R.id.listView);
        return view;
    }
    public void refresh()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    JSONObject sendJsonObject = new JSONObject();
                    long bearTime = 24*60*60*1000;
                    Long dTime = new Timestamp(System.currentTimeMillis()+bearTime).getTime();

                    Location currentLocation = new Location();
                    currentLocation.setLatitude(myListener.getLocation().getLatitude());
                    currentLocation.setLongitude(myListener.getLocation().getLongitude());

                    Gson gson = new Gson();
                    sendJsonObject.put("departureLocation",gson.toJson(currentLocation,Location.class));
                    sendJsonObject.put("targetLocation",gson.toJson(currentLocation,Location.class));
                    sendJsonObject.put("depTime",dTime);

                    String address = getResources().getString(R.string.server_addr)+getResources().getString(R.string.nearby_activity);
                    HttpHelper httpHelper = new HttpHelper();
                    boolean isSuccess = httpHelper.ServletPost(address,sendJsonObject);

                    if(isSuccess)
                    {
                        JSONObject returnJsonObject = new JSONObject(httpHelper.getResult());
                        if(returnJsonObject.has("state"))
                        {
                            int state = returnJsonObject.getInt("state");

                            switch(state)
                            {
                                case 1://注册成功，跳转到登录界面
                                    String listJson = returnJsonObject.getString("list");
                                    activityList = gson.fromJson(listJson,new TypeToken<List<CarpoolActivity>>() {
                                    }.getType());
                                    break;
                                default:
                                    Toast.makeText(getActivity(),getResources().getString(R.string.unknown_error),Toast.LENGTH_SHORT).show();
                                    break;

                            }
                        }
                        else
                        {
                            Toast.makeText(getActivity(),getResources().getString(R.string.unknown_error),Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(),getResources().getString(R.string.server_connect_fail),Toast.LENGTH_SHORT).show();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }
    @Override
    public void onStart()
    {
        super.onStart();

        locationClient = new LocationClient(getActivity().getApplicationContext());
        myListener = new MyLocationListener();
        locationClient.registerLocationListener(myListener);

        new Thread(new Runnable() {
            @Override
            public void run() {
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
        }).start();



        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

            public void onGetGeoCodeResult(GeoCodeResult result) {

                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                }

                //获取地理编码结果
            }

            @Override

            public void onGetReverseGeoCodeResult(final ReverseGeoCodeResult result) {

                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    Toast.makeText(getActivity(),getResources().getString(R.string.place_cannot_select),Toast.LENGTH_SHORT).show();
                }
                //获取反向地理编码结果
                final String address = result.getAddress();

            }
        };
        final GeoCoder mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(listener);

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        private  BDLocation location;

        @Override
        public void onReceiveLocation(BDLocation location) {
            // 非空判断
            if (location != null) {
                this.location = location;
                if(!isReady)
                {
                    refresh();
                    isReady = true;
                }
            }
        }
        public BDLocation getLocation() {
            return location;
        }

    }
}
