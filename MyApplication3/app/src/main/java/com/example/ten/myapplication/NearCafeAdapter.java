package com.example.ten.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.util.Charsets;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NearCafeAdapter extends ArrayAdapter<NearCafeData> {
    List<NearCafeData> mData;
    Context context;


    public NearCafeAdapter(@NonNull Context context, int resource, @NonNull List<NearCafeData> objects) {
        super(context, resource, objects);
        mData = objects;
        this.context = context;


    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.row, null);
        final NearCafeData p = mData.get(position); // 위젯이 하나 하나 생성될 때마다 position에 해당하는 위젯이 담긴다.
        if (p != null) {
            final View finalV1 = v;


            ImageView imgView = (ImageView) finalV1.findViewById(R.id.img);
            Picasso.with(context)
                    .load(p.getImg())
                    .into(imgView);


            //메뉴 & 가격 가져오기
            final View finalV = v;
            Ion.with(getContext())
                    .load("https://m.store.naver.com/restaurants/" + p.getId())
                    .asString(Charsets.UTF_8) // .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            TextView name = (TextView) finalV.findViewById(R.id.shortcode);


                            ArrayList<String> menuList;
                            ArrayList<String> priceList;

                            menuList = new ArrayList<>();
                            priceList = new ArrayList<>();

                            String resultString = "";
                            resultString += p.getName() + "\n";


                            String nowString1 = result;
                            int flag1 = 0;
                            int start1 = nowString1.indexOf("hourString\":\"");
                            int end1 = 0;
                            for (int j = start1; nowString1.indexOf("hourString\":\"") != -1; j++) {
                                if (nowString1.charAt(j) == '\"') {
                                    if (flag1 == 1) {
                                        start1 = j + 1;
                                    } else if (flag1 == 2) {
                                        end1 = j;
                                        String img = nowString1.substring(start1, end1);
                                        resultString += img + "\n";
                                        Log.v("done2", img + "");
                                        nowString1 = nowString1.substring(end1 + 1, nowString1.length());
                                        break;
                                    }
                                    flag1++;
                                }
                            }

                            // 메뉴 가져오기
                            String nowString = result;
                            String findStr = "<div class=\"menu_area\"><span>";
                            int findStrLen = findStr.length();
                            for (int i = 0; nowString.indexOf(findStr) != -1; i++) {
                                int start = nowString.indexOf(findStr) + findStrLen;
                                int end = 0;
                                for (int j = start + 1; ; j++) {
                                    if (nowString.charAt(j) == '>') {
                                        start = j + 1;
                                    } else if (nowString.charAt(j) == '<') {
                                        end = j;
                                        String img = nowString.substring(start, end);
                                        menuList.add(img);
                                        Log.v("done3", img + "");
                                        nowString = nowString.substring(end + 1, nowString.length());
                                        break;
                                    }
                                }
                            }


                            // 가격 가져오기
                            nowString = result;
                            findStr = "<em class=\"price\">";
                            for (int i = 0; nowString.indexOf(findStr) != -1; i++) {
                                int start = nowString.indexOf(findStr);
                                int end = 0;
                                for (int j = start + 1; ; j++) {
                                    if (nowString.charAt(j) == '>') {
                                        start = j + 1;
                                    } else if (nowString.charAt(j) == '<') {
                                        end = j - 1;
                                        String img = nowString.substring(start, end);
                                        priceList.add(img);
                                        Log.v("done3", img + "");
                                        nowString = nowString.substring(end + 1, nowString.length());
                                        break;
                                    }
                                }
                            }


                            for (int i = 0; i < menuList.size(); i++) {
                                resultString += menuList.get(i) + "  " + priceList.get(i) + "\n";
                            }
                            name.setText(resultString);
                            Log.v("resultT", resultString);
                        }
                    });
        }
        return v;

    }
}