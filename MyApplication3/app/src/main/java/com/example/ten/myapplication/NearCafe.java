package com.example.ten.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.util.Charsets;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class NearCafe extends AppCompatActivity {

    TextView textview;
    ArrayList<String> name;
    ArrayList<String> id;

    ListView nearCafeList;
    NearCafeAdapter nearCafeAdapter;
    ArrayList<NearCafeData> nearCafeDataList;

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    // GPSTracker class
    private GpsInfo gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_cafe);
        nearCafeList = (ListView) findViewById(R.id.nearCafeList);

        nearCafeDataList = new ArrayList<>();

        name = new ArrayList<>();
        id = new ArrayList<>();

        if (!isPermission) {
            callPermission();
            //return;
        }

        gps = new GpsInfo(NearCafe.this);
        // GPS 사용유무 가져오기
        double latitude=0.0;
        double longitude=0.0;
        if (gps.isGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Toast.makeText(
                    getApplicationContext(),
                    "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                    Toast.LENGTH_LONG).show();
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }

        String x = longitude+"";
        String y = latitude+"";


        Ion.with(this)
                .load("https://m.store.naver.com/places/listMap?display=40&level=middle&nlu=%5Bobject%20Object%5D&query=%EC%B9%B4%ED%8E%98&sid=468329393%2C37392371%2C967978358&sortingOrder=distance&viewType=place&x=" + x + "&y=" + y)
                .asString(Charsets.UTF_8) // .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        String nowString = result;

                        Log.v("tkdrb", nowString.indexOf("name\":\"") + "");

                        // 상호명 가져오기
                        for (int i = 0; nowString.indexOf("name\":\"") != -1; i++) {
                            int flag = 0;
                            int start = nowString.indexOf("name\":\"");
                            int end = 0;
                            for (int j = start; ; j++) {
                                if (nowString.charAt(j) == '\"') {
                                    if (flag == 1) {
                                        start = j + 1;
                                    } else if (flag == 2) {
                                        end = j;
                                        String img = nowString.substring(start, end);
                                        name.add(img);
                                        Log.v("done2", img + "");
                                        nowString = nowString.substring(end + 1, nowString.length());
                                        break;
                                    }
                                    flag++;
                                }
                            }
                        }

                        // 상호에 대한 아이디
                        String nowString1 = result;
                        for (int i = 0; nowString1.indexOf("id\":\"") != -1; i++) {
                            int flag = 0;
                            int start = nowString1.indexOf("id\":\"");
                            int end = 0;
                            for (int j = start; ; j++) {
                                if (nowString1.charAt(j) == '\"') {
                                    if (flag == 1) {
                                        start = j + 1;
                                    } else if (flag == 2) {
                                        end = j;
                                        String img = nowString1.substring(start, end);
                                        id.add(img);
                                        Log.v("done2", img + "");
                                        nowString1 = nowString1.substring(end + 1, nowString1.length());
                                        break;
                                    }
                                    flag++;
                                }
                            }
                        }

                        // 비동기 문제로 안에서 처리해준다
                        Log.v("donen", name.size() + " " + id.size());
                        for (int i = 0; i < id.size(); i++) {
                            NearCafeData nearCafeData = new NearCafeData(name.get(i), id.get(i));
                            nearCafeDataList.add(nearCafeData);
                        }
                        nearCafeAdapter = new NearCafeAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, nearCafeDataList);
                        nearCafeList.setAdapter(nearCafeAdapter);

                    }
                });
    }
//    https://m.store.naver.com/restaurants/38672904
    //권한요청
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }
}
