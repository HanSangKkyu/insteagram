package com.example.ten.myapplication;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.util.Charsets;
import com.koushikdutta.ion.Ion;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends NMapActivity implements NMapView.OnMapStateChangeListener, NMapView.OnMapViewTouchEventListener, NMapOverlayManager.OnCalloutOverlayListener {

    ImageView imageView;
    EditText editReview;
    RatingBar ratingBar;
    User m_curUser;
    NearCafeData nearCafeData;

    String nearCafeId;
    String nearCafeName;
    String urlStr;
    String curUser;

    ListView reviewList;
    ArrayList<ReviewData> reviews;
    ReviewAdapter reviewAdapter;
    TextView cafeName;
    DatabaseReference databaseReference;

    private final String CLIENT_ID = "LTOf8bZlUUyhsOXNjX43";// 애플리케이션 클라이언트 아이디 값
    private final String TAG = "MainActivity";

    private ViewGroup mapLayout;

    private NMapController mMapController = null;
    private NMapView mMapView;

    private NMapResourceProvider nMapResourceProvider;
    private NMapOverlayManager mapOverlayManager;
    NMapPOIdataOverlay.OnStateChangeListener onPOldataStateChangeListener = null;
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
    }

    public void init() {
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
        nMapResourceProvider = new NMapViewerResourceProvider(this);
        mapOverlayManager = new NMapOverlayManager(this, mMapView, nMapResourceProvider);

        int markId = NMapPOIflagType.PIN;
        NMapPOIdata nMapPOIdata = new NMapPOIdata(1, nMapResourceProvider);
        nMapPOIdata.beginPOIdata(1);

        nMapPOIdata.addPOIitem(127.0630205, 37.5091300, "Pizza777-111", markId, 0);
        nMapPOIdata.endPOIdata();

        NMapPOIdataOverlay poIdataOverlay = mapOverlayManager.createPOIdataOverlay(nMapPOIdata, null);

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

        imageView = (ImageView) findViewById(R.id.img);
        editReview = (EditText) findViewById(R.id.editReview);
        ratingBar = (RatingBar) findViewById(R.id.rating);

        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");

        // 현재 로그인한 유저
        Intent intent = getIntent();
        m_curUser = (User) intent.getSerializableExtra("user");
        urlStr = intent.getStringExtra("url");
        if (intent.getSerializableExtra("cafeid") != null) { //주변 카페
            nearCafeName = intent.getStringExtra("cafename"); //name
            nearCafeId = intent.getStringExtra("cafeid"); //id

            cafeName = (TextView) findViewById(R.id.cafeName);
            cafeName.setText(nearCafeName);

            cafeName = (TextView) findViewById(R.id.cafeName);

            final TextView cafeFood = (TextView) findViewById(R.id.cafeFood);

            Ion.with(getApplicationContext())
                    .load("https://m.store.naver.com/restaurants/" + nearCafeId)
                    .asString(Charsets.UTF_8) // .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
//                            TextView name = (TextView) finalV.findViewById(R.id.shortcode);


                            ArrayList<String> menuList;
                            ArrayList<String> priceList;

                            menuList = new ArrayList<>();
                            priceList = new ArrayList<>();

                            String resultString = "";
                            // resultString += nearCafeName + "\n";
//                            resultString += nearCafeName + "\n\n";

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
                            cafeName.setText(nearCafeName);
                            cafeFood.setText(resultString);
                            Log.v("resultT", resultString);
                        }
                    });
        }

        curUser = m_curUser.getId().toString();
        reviewList = (ListView) findViewById(R.id.reviewList);
        reviews = new ArrayList<>();

        Picasso.with(getApplicationContext())
                .load(urlStr)
                .into(imageView);

        databaseReference.child(nearCafeName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviews.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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
        if (resultCode == RESULT_OK) {
            if (requestCode == 11) {
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
    public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay nMapOverlay, NMapOverlayItem nMapOverlayItem, Rect rect) {
        return null;
    }

    public void registerReview(View view) {

        String review = editReview.getText().toString();
        double rating = ratingBar.getRating();

        if (review == null) {
            // 내용 없으면 거르기
            Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String date = simpleDateFormat.format(System.currentTimeMillis());
            ReviewData reviewData = new ReviewData(curUser, rating, review, date);
            databaseReference.child(nearCafeName).push().setValue(reviewData);
            editReview.setText("");
            Toast.makeText(this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        }

    }
}
