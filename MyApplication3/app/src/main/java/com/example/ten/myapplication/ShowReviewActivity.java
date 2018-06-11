package com.example.ten.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ShowReviewActivity extends AppCompatActivity {

    EditText editReview;
    RatingBar ratingBar;
    User m_curUser;
    String curUser;
    ListView reviewList;
    ArrayList<ReviewData> reviews;
    ReviewAdapter reviewAdapter;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_review);
        init();
    }
    public void init(){
        editReview = (EditText) findViewById(R.id.editReview);
        ratingBar = (RatingBar) findViewById(R.id.rating);

        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");

        // 현재 로그인한 유저
        Intent intent = getIntent();
        m_curUser = (User) intent.getSerializableExtra("user");
        curUser = m_curUser.getId().toString();

        reviewList = (ListView) findViewById(R.id.reviewList);
        reviews = new ArrayList<>();

        databaseReference.child("cafe1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviews.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ReviewData data = snapshot.getValue(ReviewData.class);
                    reviews.add(data);
                }
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reviewAdapter = new ReviewAdapter(getApplicationContext(), reviews);
        reviewList.setAdapter(reviewAdapter);
    }

    public void registerReview(View view) {
        String review = editReview.getText().toString();
        double rating = ratingBar.getRating();

        if(review == null) {
            // 내용 없으면 거르기
            Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = date.format(System.currentTimeMillis());
            ReviewData reviewData = new ReviewData(curUser, rating, review, strDate);
            databaseReference.child("cafe1").push().setValue(reviewData);
            editReview.setText("");
            Toast.makeText(this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
