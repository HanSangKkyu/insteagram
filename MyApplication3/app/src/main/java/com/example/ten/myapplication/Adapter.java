package com.example.ten.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.util.Charsets;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.ten.myapplication.Main2Activity.count;
import static com.example.ten.myapplication.Main2Activity.hashes;
import static com.example.ten.myapplication.Main2Activity.locationes;
import static com.example.ten.myapplication.Main2Activity.mPlaceArrayAdapter;
import static com.example.ten.myapplication.Main2Activity.filtering1;
import static com.example.ten.myapplication.Main2Activity.reality;

public class Adapter extends ArrayAdapter<Data> {
    List<Data> mData;
    Context context;
    String search; // 플라워카페, 캐릭터카페, 북카페, 루프탑카페 ...
    String tagSets = "";


    String jsontag = "";  //json으로 불용어를 제거한 다음에 실질적으로 검색에 쓰여질 변수


    public Adapter(@NonNull Context context, int resource, @NonNull List<Data> objects, String search) {
        super(context, resource, objects);
        mData = objects;
        this.context = context;
        this.search = search;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row, null);
        }
        final Data p = mData.get(position); // 위젯이 하나 하나 생성될 때마다 position에 해당하는 위젯이 담긴다.
        if (p != null) {

            ImageView img = (ImageView) v.findViewById(R.id.img);
            Picasso.with(context)
                    .load(p.getDisplay_url())
                    .into(img);

            final TextView shortcode = (TextView) v.findViewById(R.id.shortcode);
            shortcode.setText(p.getShortcode());


            // 해시태그 가져오기
            Ion.with(getContext())
                    .load("https://www.instagram.com/p/" + p.getShortcode() + "/?tagged=" + search)
                    .asString(Charsets.UTF_8) // .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {

                            String findStr = "hashtags\" content=\"";
                            int findStrLen = result.indexOf(findStr);
                            String tagSet = "";

                            String nowString = result;
                            for (int i = 0; nowString.indexOf(findStr) != -1; i++) {
                                int flag = 0;
                                int start = nowString.indexOf(findStr);
                                int end = 0;
                                for (int j = start; ; j++) {
                                    if (nowString.charAt(j) == '\"') {
                                        if (flag == 1) {
                                            start = j + 1;
                                        } else if (flag == 2) {
                                            end = j;
                                            String tag = nowString.substring(start, end);
                                            //filtering(tag);
                                            tagSet += tag + " ";
                                            try {
                                                p.getHashtag().add(tag);
                                            } catch (NullPointerException ex) {
                                                Log.v("nullEX", ex.getLocalizedMessage() + "");
                                            }
                                            nowString = nowString.substring(end + 1, nowString.length());
                                            break;
                                        }
                                        flag++;
                                    }
                                }
                            }
                            tagSets = tagSet;
                            Log.v("태그들", tagSet);
                            filtering_first(tagSets); // 자주 사용되는 태그 일차적으로 필터링
                            Log.v("태그들1", filtering1);
                            JsonParshing(filtering1, position); // 불용어 처리
                            Log.v("태그들2", mData.get(position).getPlace() + "0");
                            Log.v("태그들2json", jsontag + "0");


//                            if (!tagSets.equals("")) {
//                                filtering_first(tagSets);
//
//                                Filtering_2(filtering1, "논현");
//                            }
                        }
                    });
        }
        return v;

    }

    static public String filtering_first(String tagSet) { // 상호명이 걸러진다
        if (tagSet == "") {
            return "";
        }
        String[] str = tagSet.split(" ");
        Log.d("HashTag", str[0]);
        for (int i = 0; i < str.length; i++) {
            for (int j = 0; j < hashes.length; j++) {
                if (hashes[j].equals(str[i])) {
                    str[i] = "";
                }
            }
        }
        String temp = "";
        for (int i = 0; i < str.length; i++) {
            if (!str[i].equals(""))
                temp += str[i] + "#";
        }
        filtering1 = temp; // 상호명 들어간다
        return Arrays.toString(str).toString();
    }

    static public void Filtering_2(String str, String place) {  // 상호명과 장소를 함께 넣어주면 최종결과가 나온다
        //str은 filtering1을 거치고 난 뒤의 결과일 것
        //그거 가지고 장소검색시작한다.

        String[] s = str.split(" ");    //걸러지고 남은 아이들
        count = 0;
        do {
            Log.v("태그들4", str + "1" + place);
            ArrayList<PlaceArrayAdapter.PlaceAutocomplete> placeAutocompletes = mPlaceArrayAdapter.getPredictions(s[count], place);
            count++;
        } while (count != s.length);
    }

//    public String JsonParshing(String s, final int position) {
//        jsontag = "";
//        reality = "";
//        String temp[] = s.split(" ");
//
//        Log.v("태그들start", "-------------------");
//        for (int i = 0; i < temp.length; i++) {
//            Ion.with(getContext())
//                    .load("https://open-korean-text.herokuapp.com/extractPhrases?text=" + temp[i])
//                    .asJsonObject(Charsets.UTF_8)
//                    .setCallback(new FutureCallback<JsonObject>() {
//                        @Override
//                        public void onCompleted(Exception e, JsonObject result) {
//
//                            JsonArray array = result.getAsJsonArray("phrases");
//                            boolean flag = false;
//                            for (int i = 0; i < array.size(); i++) {
//                                //결과 값 가져오는 부분, 만약 json결과가 location결과와 동일할 경우 location 정보를 저장하고 있어야 한다.
//                                //array.get()
//                                String s = array.get(i).getAsString();
//                                int idx = s.indexOf("(");
//                                String r = s.substring(0, idx); // 불용어 단위로 나누기
//                                //Log.v("제이썬", array.get(i) + "");
//                                int idx2 = r.indexOf("카페");
//                                if (idx2 == -1)
//                                    jsontag += r + " "; // 카페가 들어가지 않은 태그들 가져오기
//                                else {
//                                    /// /String t2=r.substring()
//                                }
//                                String real1 = "";
//                                for (int j = 0; j < locationes.length; j++) {
//                                    if (locationes[j].equals(r)) {
//                                        flag = true;
//                                        real1 += r + " "; // 서울 , 동대문, 구로5동
//                                        mData.get(position).setPlace(real1);
//                                        break;
//                                    }
//                                    //  Filtering_2(filtering1, );
//                                }
////                                if (flag == true)
////                                    break;
//                            }
//                            Log.v("태그들2", mData.get(position).getPlace() + "1");
//
//                        }
//                    });
//
//        }
//
//
//        TimerTask mTask;
//        Timer mTimer;
//
//        mTask = new TimerTask() {
//            @Override
//            public void run() {
//
//                Filtering_2(jsontag, mData.get(position).getPlace());
//            }
//        };
//
//        mTimer = new Timer();
//        mTimer.schedule(mTask, 1000);
//
//
//        return null;
//    }

    public String JsonParshing(String s, final int position) {
        jsontag = "";
        reality = "";
        final String temp[] = s.split(" ");

        Log.v("태그들start", "-------------------");
        for (int i = 0; i < temp.length; i++) {
            Ion.with(getContext())
                    .load("https://open-korean-text.herokuapp.com/extractPhrases?text=" + temp[i])
                    .asJsonObject(Charsets.UTF_8)
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            JsonArray array = result.getAsJsonArray("phrases");
                            boolean flag = false;
                            for (int i = 0; i < array.size(); i++) {
                                //결과 값 가져오는 부분, 만약 json결과가 location결과와 동일할 경우 location 정보를 저장하고 있어야 한다.
                                //array.get()
                                String s = array.get(i).getAsString();
                                int idx = s.indexOf("(");
                                String r = s.substring(0, idx); // 불용어 단위로 나누기
                                //Log.v("제이썬", array.get(i) + "");
                                int idx2 = r.indexOf("카페");
                                if (idx2 == -1)
                                    jsontag += r + " "; // 카페가 들어가지 않은 태그들 가져오기
                                else {
                                    /// /String t2=r.substring()
                                }
                                if (i == temp.length - 1) { // 마지막 실행이면
                                    TimerTask mTask;
                                    Timer mTimer;

                                    mTask = new TimerTask() {
                                        @Override
                                        public void run() {
                                            String temp2[] = jsontag.split(" "); // 불용어 처리를 위해

                                            String real1 = "";
                                            for (int j = 0; j < locationes.length; j++) {
                                                if (locationes[j].equals(temp2)) {
                                                    real1 += temp2 + " "; // 서울 , 동대문, 구로5동
                                                    mData.get(position).setPlace(real1);
                                                    break;
                                                }
                                                //  Filtering_2(filtering1, );
                                            }

                                            Filtering_2(jsontag, mData.get(position).getPlace());
                                        }
                                    };

                                    mTimer = new Timer();
                                    mTimer.schedule(mTask, 1000);
                                }
                            }
                        }
                    });
        }


        return null;
    }


//
//    public String filtering_two(String tagSet) {
//        /*2차필터링-준비단계
//        1. 단어에서 조사나 어미 등 불용어를 제거하는 과정 필요
//        2. 어구를 추출하고 추출하여 나온 어구중 가장 길이가 긴 어구는 뺄 것
//        *
//        * */
//        return null;
//    }
}
