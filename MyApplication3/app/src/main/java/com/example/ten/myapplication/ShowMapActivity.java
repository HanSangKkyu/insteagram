package com.example.ten.myapplication;

import android.os.Bundle;
import android.view.ViewGroup;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;

public class ShowMapActivity extends NMapActivity {

    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "LTOf8bZlUUyhsOXNjX43";// 애플리케이션 클라이언트 아이디 값
    private ViewGroup mapLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_map);
        init();

        // java code

    }
    public void init(){
        mapLayout = findViewById(R.id.map_view);

        mMapView = new NMapView(this);
        mMapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.setScalingFactor(1.5f);
        mMapView.requestFocus();

        mapLayout.addView(mMapView);



    }
}
