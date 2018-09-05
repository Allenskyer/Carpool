package com.tj.carpool.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.tj.carpool.CreateCarpoolActivity;
import com.tj.carpool.R;


import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateCarpoolFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateCarpoolFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateCarpoolFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient locationClient = null;
    private MyLocationListener myListener = null;
    ImageButton locateButton;
    Button startButton;
    EditText originEditText,destEditText;
    String oriAdress,destAdress;
    double oriLongitude, destLongitude, oriLatitude, destLatitude;
    Button originButton,destButton;
    LinearLayout originLayout,destLayout;

    int state = 0;//0：正常状态，1：等待点选起点，2：等待点选终点

    public CreateCarpoolFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateCarpoolFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateCarpoolFragment newInstance() {
        CreateCarpoolFragment fragment = new CreateCarpoolFragment();

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
        View view = inflater.inflate(R.layout.fragment_create_carpool, container, false);
        mMapView = (MapView)view.findViewById(R.id.mmap);
        mBaiduMap = mMapView.getMap();
        locateButton = (ImageButton) view.findViewById(R.id.btn_loc);

        startButton = (Button) view.findViewById(R.id.btn_start);
        originEditText = (EditText) view.findViewById(R.id.edit_origin);
        destEditText = (EditText) view.findViewById(R.id.edit_dest);

        originButton = (Button) view.findViewById(R.id.btn_origin);
        destButton = (Button) view.findViewById(R.id.btn_dest);

        originLayout = (LinearLayout) view.findViewById(R.id.origin_layout);
        destLayout = (LinearLayout) view.findViewById(R.id.dest_layout);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        locationClient = new LocationClient(getActivity().getApplicationContext());
        myListener = new MyLocationListener();
        locationClient.registerLocationListener(myListener);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式 高精度
        option.setCoorType("bd09ll");// 设置返回定位结果是百度经纬度 默认gcj02
        option.setScanSpan(1000);// 设置发起定位请求的时间间隔 单位ms
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息
        //option.setNeedDeviceDirect(true);// 设置定位结果包含手机机头 的方向
        // 设置定位参数
        locationClient.setLocOption(option);
        locationClient.start();

        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng ll = new LatLng(myListener.getLocation().getLatitude(),myListener.getLocation().getLongitude());
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                //mBaiduMap.setMapStatus(status);//直接到中间
                mBaiduMap.animateMapStatus(status);//动画的方式到中间
            }
        });

        originButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                originLayout.setVisibility(View.GONE);
                destLayout.setVisibility(View.GONE);
                state = 1;
                Toast.makeText(getActivity(),getResources().getString(R.string.click_operation),Toast.LENGTH_LONG).show();

            }
        });
        destButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originLayout.setVisibility(View.GONE);
                destLayout.setVisibility(View.GONE);
                state = 2;
                Toast.makeText(getActivity(),getResources().getString(R.string.click_operation),Toast.LENGTH_LONG).show();
            }
        });


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String origin = originEditText.getText().toString().replace(" ","");
                String destination = destEditText.getText().toString().replace(" ","");

                if(origin.length()<1)
                {
                    Toast.makeText(getActivity(),getResources().getString(R.string.blank_origin),Toast.LENGTH_SHORT).show();
                }
                else if(destination.length()<1)
                {
                    Toast.makeText(getActivity(),getResources().getString(R.string.blank_dest),Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Intent intent = new Intent(getActivity(), CreateCarpoolActivity.class);
                    intent.putExtra("ID",getArguments().getInt("ID"));
                    intent.putExtra("oriAddress",oriAdress);
                    intent.putExtra("destAddress",destAdress);
                    intent.putExtra("oriLatitude",oriLatitude);
                    intent.putExtra("oriLongitude",oriLongitude);
                    intent.putExtra("destLatitude",destLatitude);
                    intent.putExtra("destLongitude",destLongitude);

                    startActivity(intent);
                }
            }
        });

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
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(getResources().getString(R.string.confirm_place));
                dialog.setMessage(address);
                dialog.setCancelable(false);
                dialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (state)
                        {
                            case 1://添加起点
                                oriAdress = address;
                                oriLatitude = result.getLocation().latitude;
                                oriLongitude = result.getLocation().longitude;
                                originEditText.setText(address);
                                break;
                            case 2:
                                destAdress = address;
                                destLatitude = result.getLocation().latitude;
                                destLongitude = result.getLocation().longitude;
                                destEditText.setText(address);
                                break;
                            default:
                                break;
                        }
                        state = 0;
                        originLayout.setVisibility(View.VISIBLE);
                        destLayout.setVisibility(View.VISIBLE);
                        //mBaiduMap.clear();
                    }
                });
                dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //取消则不进行任何操作
                    }
                });
                dialog.show();
            }
        };
        final GeoCoder mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(listener);


        BaiduMap.OnMapClickListener onMapClickListener = new BaiduMap.OnMapClickListener() {
            /**
             * 地图单击事件回调函数
             * @param point 点击的地理坐标
             */
            public void onMapClick(LatLng point){

//                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_menu_send);
//
//                //构建MarkerOption，用于在地图上添加Marker
//                OverlayOptions option = new MarkerOptions()
//                        .position(point)
//                        .icon(bitmap);
                switch(state)
                {
                    case 1:
                    case 2:
                        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                                .location(point));
                        break;
                    default:
                        break;
                }
            }
            /**
             * 地图内 Poi 单击事件回调函数
             * @param poi 点击的 poi 信息
             */
            public boolean onMapPoiClick(MapPoi poi){
                return false;
            }
        };
        mBaiduMap.setOnMapClickListener(onMapClickListener);

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

        boolean firstLocate = true;
        @Override
        public void onReceiveLocation(BDLocation location) {
            // 非空判断
            if (location != null) {
                this.location = location;
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String address = location.getAddrStr();
                Log.i(TAG, "address:" + address + " latitude:" + latitude
                        + " longitude:" + longitude + "---");
                //Toast.makeText(NearbyActivity.this,location.getAddrStr(),Toast.LENGTH_SHORT).show();
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();

                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);

                if(firstLocate)
                {
                    firstLocate = false;
                    LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                    //mBaiduMap.setMapStatus(status);//直接到中间
                    mBaiduMap.animateMapStatus(status);//动画的方式到中间
                }

            }
        }
        public BDLocation getLocation() {
            return location;
        }

    }
}
