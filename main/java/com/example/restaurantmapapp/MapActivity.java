package com.example.restaurantmapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.example.restaurantmapapp.db.DatabaseAdapter;
import com.example.restaurantmapapp.db.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MapActivity extends Activity implements BaiduMap.OnMapClickListener, BaiduMap.OnMarkerClickListener {
    // 地图View实例
    private MapView mMapView = null;
    private TextView mPoiTitle = null;

    private TextView mPoiAddress = null;
    private BaiduMap mBaiduMap = null;
    private LinearLayout mLayoutDetailInfo = null;
    private Marker mPreSelectMarker = null;

    private BitmapDescriptor mBitmapDescWaterDrop =
            BitmapDescriptorFactory.fromResource(R.drawable.water_drop);

    private HashMap<Marker, PoiInfo> mMarkerPoiInfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initMap();
        Intent intent = getIntent();
        //定义Maker坐标点
        LatLng point = intent.getParcelableExtra("latLng");
        if (point != null) {
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(mBitmapDescWaterDrop);
//在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
        } else {
            DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
            ArrayList<Place> allPlace = databaseAdapter.findAllPlace();
            ArrayList<LatLng> latLngs = new ArrayList<>();
            for (Place place : allPlace) {
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                latLngs.add(latLng);
                OverlayOptions option1 = new MarkerOptions()
                        .position(latLng)
                        .icon(mBitmapDescWaterDrop);
                //在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option1);
            }
            // 将地图平移到 latLng 位置
            LatLng latLng = latLngs.get(0);
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.setMapStatus(mapStatusUpdate);
            setBounds(latLngs);
        }

    }

    private void setPoiResult(List<PoiInfo> poiInfos) {
        if (null == poiInfos || poiInfos.size() <= 0) {
            return;
        }

        clearData();

        // 将地图平移到 latLng 位置
        LatLng latLng = poiInfos.get(0).getLocation();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        Iterator itr = poiInfos.iterator();
        List<LatLng> latLngs = new ArrayList<>();
        PoiInfo poiInfo = null;
        int i = 0;
        while (itr.hasNext()) {
            poiInfo = (PoiInfo) itr.next();
            if (null == poiInfo) {
                continue;
            }

            locatePoiInfo(poiInfo, i);
            latLngs.add(poiInfo.getLocation());
            if (0 == i) {
                showPoiInfoLayout(poiInfo);
            }

            i++;
        }

        setBounds(latLngs);
    }

    /**
     * 最佳视野内显示所有点标记
     */
    private void setBounds(List<LatLng> latLngs) {
        if (null == latLngs || latLngs.size() <= 0) {
            return;
        }

        int horizontalPadding = 80;
        int verticalPaddingBottom = 400;

        // 构造地理范围对象
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // 让该地理范围包含一组地理位置坐标
        builder.include(latLngs);

        // 设置显示在指定相对于MapView的padding中的地图地理范围
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(),
                horizontalPadding,
                verticalPaddingBottom,
                horizontalPadding,
                verticalPaddingBottom);
        // 更新地图
        mBaiduMap.setMapStatus(mapStatusUpdate);
        // 设置地图上控件与地图边界的距离，包含比例尺、缩放控件、logo、指南针的位置
        mBaiduMap.setViewPadding(0,
                0,
                0,
                verticalPaddingBottom);
    }

    /**
     * 显示底部poi详情
     *
     * @param poiInfo
     */
    private void showPoiInfoLayout(PoiInfo poiInfo) {

        if (null == mLayoutDetailInfo || null == poiInfo) {
            return;
        }

        if (null == mPoiTitle) {
            return;
        }

        if (null == mPoiAddress) {
            return;
        }

        mLayoutDetailInfo.setVisibility(View.VISIBLE);

        mPoiTitle.setText(poiInfo.getName());

        String address = poiInfo.getAddress();
        if (TextUtils.isEmpty(address)) {
            mPoiAddress.setVisibility(View.GONE);
        } else {
            mPoiAddress.setText(poiInfo.getAddress());
            mPoiAddress.setVisibility(View.VISIBLE);
        }
    }

    private void locatePoiInfo(PoiInfo poiInfo, int i) {
        if (null == poiInfo) {
            return;
        }

        // 隐藏输入法
        Utils.hideKeyBoard(this);

        // 显示当前的
        showPoiMarker(poiInfo, i);
    }

    private void showPoiMarker(PoiInfo poiInfo, int i) {
        if (null == poiInfo) {
            return;
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(poiInfo.getLocation())
                .icon(mBitmapDescWaterDrop);

        // 第一个poi放大显示
        if (0 == i) {
            InfoWindow infoWindow = getPoiInfoWindow(poiInfo);
            markerOptions.scaleX(1.5f).scaleY(1.5f).infoWindow(infoWindow);
        }

        Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
        if (null != marker) {
            mMarkerPoiInfo.put(marker, poiInfo);

            if (0 == i) {
                mPreSelectMarker = marker;
            }
        }
    }

    private InfoWindow getPoiInfoWindow(PoiInfo poiInfo) {
        TextView textView = new TextView(this);
        textView.setText(poiInfo.getName());
        textView.setPadding(10, 5, 10, 5);
        textView.setBackground(this.getResources().getDrawable(R.drawable.bg_info));
        InfoWindow infoWindow = new InfoWindow(textView, poiInfo.getLocation(), -150);
        return infoWindow;
    }

    private void clearData() {
        mBaiduMap.clear();
        mMarkerPoiInfo.clear();
        mPreSelectMarker = null;
    }


    private void initMap() {
        mMapView = findViewById(R.id.mapview);
        if (null == mMapView) {
            return;
        }

        mBaiduMap = mMapView.getMap();
        if (null == mBaiduMap) {
            return;
        }

        // 解决圆角屏幕手机，地图logo被遮挡的问题
        mBaiduMap.setViewPadding(30, 0, 30, 20);
        mMapView.showZoomControls(false);

        // 设置初始中心点为北京
        LatLng center = new LatLng(39.963175, 116.400244);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(center, 12);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mMapView) {
            mMapView.onDestroy();
        }

        if (null != mBitmapDescWaterDrop) {
            mBitmapDescWaterDrop.recycle();
        }
    }
}
