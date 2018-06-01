package com.example.ten.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.util.Charsets;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;

import java.util.ArrayList;

public class InstargramAPI extends AppCompatActivity {

    TextView tv;
    ArrayList<String> imgUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instargram_api);
        tv = (TextView) findViewById(R.id.tv);
        //    https://api.instagram.com/v1/tags/nofilter/media/recent?access_token=4796908241.9ab492e.4cf6b822cecd43b0a415442780d05d6b
        //    https://api.instagram.com/v1/tags/{tag-name}?access_token=ACCESS-TOKEN

        // 카페라는 태그를 가진 게시글수 보여주기
//        Ion.with(this)
//                .load("https://api.instagram.com/v1/tags/카페?access_token=4796908241.9ab492e.4cf6b822cecd43b0a415442780d05d6b")
//                .asString(Charsets.UTF_8) // .asString()
//                .setCallback(new FutureCallback<String>() {
//                    @Override
//                    public void onCompleted(Exception e, String result) {
//                    }
//                });
        imgUrlList = new ArrayList<>();

        Ion.with(this)
                .load("https://www.instagram.com/explore/tags/%EC%9B%A8%EB%94%A9%EC%B9%B4%ED%8E%98/?hl=ko")
                .asString(Charsets.UTF_8) // .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        tv.setText(String.valueOf(result));



                        // 최신글의 이미지를 가져온다.
                        String nowString = String.valueOf(result);
                        for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
                            int flag = 0;
                            int start = nowString.indexOf("display_url");
                            int end = 0;
                            for (int j = start; ; j++) {
                                if (nowString.charAt(j) == '\"') {
                                    if (flag == 1) {
                                        start = j + 1;
                                    } else if (flag == 2) {
                                        end = j;
                                        String img = nowString.substring(start, end);
                                        imgUrlList.add(img);
                                        Log.v("asdf", img + "");
                                        nowString = nowString.substring(end + 1, nowString.length());
                                        break;
                                    }
                                    flag++;
                                }
                            }
                        }
                    }
                });


    }


}
