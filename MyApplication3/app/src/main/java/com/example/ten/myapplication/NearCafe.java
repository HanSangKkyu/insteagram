package com.example.ten.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_cafe);
        nearCafeList = (ListView) findViewById(R.id.nearCafeList);

        nearCafeDataList = new ArrayList<>();

        name = new ArrayList<>();
        id = new ArrayList<>();


        String x = "126.92867389999999";
        String y = "37.5085523";


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
}
