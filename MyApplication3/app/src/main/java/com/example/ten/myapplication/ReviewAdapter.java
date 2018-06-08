package com.example.ten.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewAdapter extends BaseAdapter {

    ArrayList<ReviewData> list= new ArrayList<ReviewData>();

    public ReviewAdapter() {

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int pos = i;
        final Context context = viewGroup.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.review, viewGroup, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView id = (TextView) view.findViewById(R.id.r_id) ;
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.r_rating) ;
        TextView review = (TextView) view.findViewById(R.id.r_review) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ReviewData listViewItem = list.get(i);

        // 아이템 내 각 위젯에 데이터 반영
        id.setText(listViewItem.getId());
        ratingBar.setRating((float) listViewItem.getRating());
        review.setText(listViewItem.getReview());

        return view;
    }
}
