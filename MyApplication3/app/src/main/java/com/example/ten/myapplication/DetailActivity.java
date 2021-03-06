package com.example.ten.myapplication;

import android.content.Intent;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
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

import com.google.android.gms.maps.model.LatLng;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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

    String instaCafeName;
    String instaCafeAddress;

    ListView reviewList;
    ArrayList<ReviewData> reviews;
    ReviewAdapter reviewAdapter;
    TextView cafeName;
    DatabaseReference databaseReference;
    List<Address> result;
    LatLng latLng;

    RatingBar averageRating;

    private final String CLIENT_ID = "LTOf8bZlUUyhsOXNjX43";// 애플리케이션 클라이언트 아이디 값
    private final String TAG = "MainActivity";

    private ViewGroup mapLayout;

    private NMapController mMapController = null;
    private NMapView mMapView;

    private NMapResourceProvider nMapResourceProvider;
    private NMapOverlayManager mapOverlayManager;
    NMapPOIdataOverlay.OnStateChangeListener onPOldataStateChangeListener = null;
    SimpleDateFormat simpleDateFormat;
    Geocoder geocoder;  //주어진 주소에서 위도, 경도를 가져와야함.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
    }

    public void addressToLatLng(String adrress) {
        try {
            result = geocoder.getFromLocationName(adrress, 1);
            if (result.size() > 0) {
                Address address = result.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                setMarker(latLng);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMarker(LatLng latLng) {

        int markId = NMapPOIflagType.PIN;
        NMapPOIdata nMapPOIdata = new NMapPOIdata(1, nMapResourceProvider);
        nMapPOIdata.beginPOIdata(1);

        nMapPOIdata.addPOIitem(latLng.longitude, latLng.latitude, nearCafeName, markId, 0);
        nMapPOIdata.endPOIdata();

        NMapPOIdataOverlay poIdataOverlay = mapOverlayManager.createPOIdataOverlay(nMapPOIdata, null);

        poIdataOverlay.showAllPOIdata(0);
        poIdataOverlay.setOnStateChangeListener(onPOldataStateChangeListener);
        mMapView.setScalingFactor(1.7f);

        mMapController = mMapView.getMapController();
        mMapController.setMapCenter(new NGeoPoint(latLng.longitude, latLng.latitude), 10);     //Default Data
    }

    public void init() {
        geocoder = new Geocoder(this);


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

        if (intent.getSerializableExtra("cafeid") != null) { //주변 카페(인스타는 cafeid가 없다)
            nearCafeName = intent.getStringExtra("cafename"); //name
            nearCafeId = intent.getStringExtra("cafeid"); //id

            cafeName = (TextView) findViewById(R.id.cafeName); // 카페이름 텍스트뷰 설정
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

                            // 시간정보
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
                                        resultString += img + "\n\n";
                                        Log.v("done2", img + "");
                                        nowString1 = nowString1.substring(end1 + 1, nowString1.length());
                                        break;
                                    }
                                    flag1++;
                                }
                            }

                            // 지도정보
                            String nowString2 = result;
                            int flag2 = 0;
                            int start2 = nowString2.indexOf("roadAddr\":\"");
                            int end2 = 0;
                            for (int j = start2; nowString2.indexOf("roadAddr\":\"") != -1; j++) {
                                if (nowString2.charAt(j) == '\"') {
                                    if (flag2 == 1) {
                                        start2 = j + 1;
                                    } else if (flag2 == 2) {
                                        end2 = j;
                                        String img = nowString2.substring(start2, end2);
                                        resultString += img + "\n\n";
                                        Log.v("roadArr", img + "");
                                        nowString2 = nowString2.substring(end2 + 1, nowString2.length());
                                        addressToLatLng(nowString2);
                                        break;
                                    }
                                    flag2++;
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

                            if(nearCafeName.length() == 0 ) {
                                cafeName.setText("장소 정보를 찾을 수 없습니다.");
                                cafeFood.setText("태그로 장소 정보를 찾는 데 실패했습니다.");
                            } else {
                                cafeName.setText(nearCafeName);
                                cafeFood.setText(resultString);
                            }
//                            cafeName.setText(nearCafeName);
//                            cafeFood.setText(resultString);
                            Log.v("resultT", resultString);
                        }
                    });
        } else { // 인스타에서 가져오는 게시글
            Log.v("들어감", urlStr + " / " + nearCafeName + " / " + instaCafeAddress + " / ");

            // 이미지 가져오기
            Picasso.with(getApplicationContext())
                    .load(urlStr)
                    .into(imageView);

            // 카페이름 텍스트뷰 설정
            nearCafeName = intent.getStringExtra("cafename");
            cafeName = (TextView) findViewById(R.id.cafeName);

            if(nearCafeName.length() == 0) {
                cafeName.setText("장소 정보를 찾을 수 없습니다.");
            }
            else {
                cafeName.setText(nearCafeName);
            }

            // 지도 마커 찍기
            instaCafeAddress = intent.getStringExtra("Address");
            addressToLatLng(instaCafeAddress);

            TextView cafeFood = (TextView) findViewById(R.id.cafeFood);
            cafeFood.setText("");
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

                float ratingSum = 0;

                if (nearCafeName.length() != 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ReviewData data = snapshot.getValue(ReviewData.class);
                        ratingSum += data.getRating();
                        reviews.add(data);
                    }
                    reviewAdapter.notifyDataSetChanged();
                }

                // 평점 평균 계산
                float average = ratingSum / reviews.size();
                averageRating.setRating(average);
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

        averageRating = (RatingBar) findViewById(R.id.averageRating);
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
        i.putExtra("cafename", nearCafeName);
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
