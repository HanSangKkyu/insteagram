package com.example.ten.myapplication;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ShowMapActivity extends NMapActivity implements NMapView.OnMapStateChangeListener, NMapView.OnMapViewTouchEventListener, NMapOverlayManager.OnCalloutOverlayListener{

    private final String CLIENT_ID = "LTOf8bZlUUyhsOXNjX43";// 애플리케이션 클라이언트 아이디 값
    private final String  TAG = "MainActivity";

    private ViewGroup mapLayout;

    private NMapController mMapController=null;
    private NMapView mMapView;

    private NMapResourceProvider nMapResourceProvider;
    private NMapOverlayManager mapOverlayManager;
    NMapPOIdataOverlay.OnStateChangeListener onPOldataStateChangeListener=null;

    EditText editReview;
    RatingBar ratingBar;
    String curUser;
    ListView reviewList;
    ArrayList<ReviewData> reviews;
    ReviewAdapter reviewAdapter;

    DatabaseReference databaseReference;

    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_map);
        init();
    }

    public void init(){
        mapLayout = findViewById(R.id.map_view);

        mMapView = new NMapView(this);
        mMapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.setScalingFactor(1.7f);
        mMapView.requestFocus();

        mapLayout.addView(mMapView);
        nMapResourceProvider=new NMapViewerResourceProvider(this);
        mapOverlayManager=new NMapOverlayManager(this, mMapView, nMapResourceProvider);

        int markId=NMapPOIflagType.PIN;
        NMapPOIdata nMapPOIdata=new NMapPOIdata(1, nMapResourceProvider);
        nMapPOIdata.beginPOIdata(1);

        nMapPOIdata.addPOIitem(127.0630205, 37.5091300, "Pizza777-111", markId, 0);
        nMapPOIdata.endPOIdata();

        NMapPOIdataOverlay poIdataOverlay=mapOverlayManager.createPOIdataOverlay(nMapPOIdata, null);

        poIdataOverlay.showAllPOIdata(0);
        poIdataOverlay.setOnStateChangeListener(onPOldataStateChangeListener);
        mMapView.setScalingFactor(1.7f);

        mMapController = mMapView.getMapController();
        mMapController.setMapCenter(new NGeoPoint(127.0630205, 37.5091300), 11);     //Default Data

        //Default Data

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //setMarker();
            }
        }, 5000);


        editReview = (EditText) findViewById(R.id.editReview);
        ratingBar = (RatingBar) findViewById(R.id.rating);

        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");

        // 현재 로그인한 유저
        Intent intent = getIntent();
        curUser = intent.getStringExtra("user");

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

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    }

    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {

    }

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {

    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {

    }

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

    }

    @Override
    public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {

    }

    @Override
    public void onLongPressCanceled(NMapView nMapView) {

    }

    @Override
    public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {

    }

    @Override
    public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {

    }

    @Override
    public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {

    }

    @Override
    public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {

    }

    @Override
    public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0, NMapOverlayItem arg1, Rect arg2) {
        return new NMapCalloutBasicOverlay(arg0, arg1, arg2);
    }

    public void BtnClick(View view) {
        //Btn을 누르면 네이버 지도앱으로 이동.
        String url ="https://m.map.naver.com/directions/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
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

    public void GoGPS(View view) {
        Intent intent=new Intent(this, NearCafe.class);
        startActivity(intent);
    }
}

