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

import java.util.List;

public class Adapter extends ArrayAdapter<Data> {
    List<Data> mData;
    Context context;
    String search; // 플라워카페, 캐릭터카페, 북카페, 루프탑카페 ...
    String tagSets="";

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
                            //String r = filtering_first(tagSet);
//                            shortcode.setText("\n" + r);
                            tagSets=tagSet;

                        }
                    });
        }
        return v;

    }


    public String filtering_two(String tagSet) {
        /*2차필터링-준비단계
        1. 단어에서 조사나 어미 등 불용어를 제거하는 과정 필요
        2. 어구를 추출하고 추출하여 나온 어구중 가장 길이가 긴 어구는 뺄 것
        *
        * */
        return null;
    }
}
