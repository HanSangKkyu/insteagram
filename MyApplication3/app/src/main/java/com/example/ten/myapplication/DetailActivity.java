package com.example.ten.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editReview;
    RatingBar ratingBar;
    User m_curUser;

    String urlStr;
    String curUser;

    ListView reviewList;
    ArrayList<ReviewData> reviews;
    ReviewAdapter reviewAdapter;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
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
                reviewAdapter.notifyDataSetChanged();
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
}
