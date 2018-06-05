package com.example.ten.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.util.Charsets;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Scanner;

public class InstargramAPI extends AppCompatActivity {

    ArrayList<String> imgUrlList;
    ArrayList<String> urlList;
    ArrayList<String> hashtagList;
    ArrayList<Data> dataList;
    ListView lsitView;
    Adapter adapter;
    static String[] hashtag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instargram_api);
        makeFile();
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
                .load("https://www.instagram.com/explore/tags/%ED%94%8C%EB%9D%BC%EC%9B%8C%EC%B9%B4%ED%8E%98/?hl=ko")
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
    }
    public void makeFile(){
        String hash="";
        Scanner scan=new Scanner(getResources().openRawResource(R.raw.hash));
        while(scan.hasNextLine()){
            hash+=scan.nextLine();
        }
        hashtag=hash.split(" ");
        //Toast.makeText(this, hashtag[1], Toast.LENGTH_SHORT).show();;
    }
}
