package com.example.ten.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editReview;
    RatingBar ratingBar;
    User m_curUser;

    String urlStr;
    String curUser;
    String cafeName; // 카페 이름 어디서 알아낸담

    ListView reviewList;
    ArrayList<ReviewData> reviews;
    ReviewAdapter reviewAdapter;

    DatabaseReference databaseReference;

    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
    }
    public void init(){
        imageView = (ImageView) findViewById(R.id.img);
        editReview = (EditText) findViewById(R.id.editReview);
        ratingBar = (RatingBar) findViewById(R.id.rating);

        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");

        // 현재 로그인한 유저
        Intent intent = getIntent();
        m_curUser = (User) intent.getSerializableExtra("user");
        urlStr = intent.getStringExtra("url");

        curUser = m_curUser.getId().toString();
        reviewList = (ListView) findViewById(R.id.reviewList);
        reviews = new ArrayList<>();

        Picasso.with(getApplicationContext())
                .load(urlStr)
                .into(imageView);

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
        reviewList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                reviewList.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 11){
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showReview(View view) {
        Intent i = new Intent(this, ShowReviewActivity.class);
        i.putExtra("user", m_curUser);
        startActivity(i);
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
            String date = simpleDateFormat.format(System.currentTimeMillis());
            ReviewData reviewData = new ReviewData(curUser, rating, review, date);
            databaseReference.child("cafe1").push().setValue(reviewData);
            editReview.setText("");
            Toast.makeText(this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        }

    }
}
