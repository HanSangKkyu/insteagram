package com.example.ten.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
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

    ArrayList<String> imgUrlList;
    ArrayList<String> urlList;
    ArrayList<String> hashtagList;
    ArrayList<Data> dataList;
    ListView lsitView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instargram_api);
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
        urlList = new ArrayList<>();  /////
        hashtagList = new ArrayList<>();
        lsitView = (ListView) findViewById(R.id.listView);
        dataList = new ArrayList<>();

        Ion.with(this)
                .load("https://www.instagram.com/explore/tags/%EC%9B%A8%EB%94%A9%EC%B9%B4%ED%8E%98/?hl=ko")
                .asString(Charsets.UTF_8) // .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
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

                        // 최신글의 url을 가져온다.
                        String nowString1 = String.valueOf(result);
                        for (int i = 0; nowString1.indexOf("shortcode") != -1; i++) {
                            int flag = 0;
                            int start = nowString1.indexOf("shortcode");
                            int end = 0;
                            for (int j = start; ; j++) {
                                if (nowString1.charAt(j) == '\"') {
                                    if (flag == 1) {
                                        start = j + 1;
                                    } else if (flag == 2) {
                                        end = j;
                                        String img = nowString1.substring(start, end);
                                        urlList.add(img);
                                        Log.v("asdf", img + "");
                                        nowString1 = nowString1.substring(end + 1, nowString1.length());
                                        break;
                                    }
                                    flag++;
                                }
                            }
                        }

                        Log.v("donen", imgUrlList.size() + "");

                        for (int i = 0; i < imgUrlList.size(); i++) {
                            Data data = new Data(imgUrlList.get(i), urlList.get(i));
                            dataList.add(data);
                        }
                        adapter = new Adapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, dataList);
                        lsitView.setAdapter(adapter);
                    }
                });


        // 해시태그 가져오기
//        Ion.with(this)
//                .load("https://www.instagram.com/p/BjeYic0nI_B/?hl=ko&tagged=%EC%9B%A8%EB%94%A9%EC%B9%B4%ED%8E%98")
//                .asString(Charsets.UTF_8) // .asString()
//                .setCallback(new FutureCallback<String>() {
//                    @Override
//                    public void onCompleted(Exception e, String result) {
//
//                        String findStr = "hashtags\" content=\"";
//                        int findStrLen = result.indexOf(findStr);
//
//
//                        String nowString = result;
//                        for (int i = 0; nowString.indexOf(findStr) != -1; i++) {
//                            int flag = 0;
//                            int start = nowString.indexOf(findStr);
//                            int end = 0;
//                            for (int j = start; ; j++) {
//                                if (nowString.charAt(j) == '\"') {
//                                    if (flag == 1) {
//                                        start = j + 1;
//                                    } else if (flag == 2) {
//                                        end = j;
//                                        String tag = nowString.substring(start, end);
//                                        hashtagList.add(tag);
//                                        Log.v("asdf", tag + "");
//                                        nowString = nowString.substring(end + 1, nowString.length());
//                                        break;
//                                    }
//                                    flag++;
//                                }
//                            }
//                        }
//
//                    }
//                });
    }
}
