package com.example.ten.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.util.Charsets;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Scanner;


public class Main2Activity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    int m_prefSize; //관심사 개수
    static String[] m_data; //관심사 저장한 배열
    static User m_user;
    String m_pref;
    DatabaseReference rDatabase;
    Intent intent;
    public static final int TYPE_CAFE = 15;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(33.500000, 126.51667), new LatLng(37.56667, 126.97806));
    private AutocompleteFilter typeFilter;
    private static final String LOG_TAG = "Main2Activity";
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final String TAG = "PlaceArrayAdapter";


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();
    }

    public void init() {
        rDatabase = FirebaseDatabase.getInstance().getReference("users");
        intent = getIntent();
        final User user = (User) intent.getSerializableExtra("user");
        m_user = user;
        m_pref = user.getPreferences();
        m_pref = m_pref.substring(0, m_pref.length() - 1);
        m_data = m_pref.split(" ");
        m_prefSize = m_data.length;

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Change preferences", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), ChangePrefActivity.class);
                intent.putExtra("users", user);
                startActivity(intent);
            }
        });


        //여리 태그에 따른 카페 주소 찾아내기
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(TYPE_CAFE)
                .build();
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, typeFilter);
        //mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
        mPlaceArrayAdapter.mGoogleApiClient=mGoogleApiClient;
        Filtering_2();

    }
    public void Filtering_2(){
        ArrayList<PlaceArrayAdapter.PlaceAutocomplete> placeAutocompletes = mPlaceArrayAdapter.getPredictions("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ListView listView;


        // 관심사 카페 보여주기 위한 필드들
        ArrayList<String> imgUrlList;
        ArrayList<String> urlList;
        ArrayList<String> hashtagList;
        ArrayList<Data> dataList;
        ListView lsitView;
        Adapter adapter;
        static String[] hashtag1;


        // 집 앞 카페 보여주기 위한 피드들
        TextView textview;
        ArrayList<String> name;
        ArrayList<String> id;
        ArrayList<String> imgList;

        ListView nearCafeList;
        NearCafeAdapter nearCafeAdapter;
        ArrayList<NearCafeData> nearCafeDataList;

        final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
        final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
        boolean isAccessFineLocation = false;
        boolean isAccessCoarseLocation = false;
        boolean isPermission = false;


        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        //여기가 layout 부분
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);

            makeFile();

            int size = m_data.length;
            if(size == 1) {
                if (sectionNumber == 1) {
                    rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                    final String search = m_data[sectionNumber - 1];


                    listView = (ListView) rootView.findViewById(R.id.listView);
                    //callList();
                    imgUrlList = new ArrayList<>();
                    urlList = new ArrayList<>();  /////
                    hashtagList = new ArrayList<>();
                    dataList = new ArrayList<>();


                    Ion.with(this)
                            .load("https://www.instagram.com/explore/tags/" + search + "/?hl=ko")
                            .asString(Charsets.UTF_8) // .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    // 최신글의 이미지를 가져온다.
                                    String nowString = String.valueOf(result);
                                    for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString.indexOf("display_url");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString.substring(start, end);
                                                    imgUrlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString = nowString.substring(end + 1, nowString.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }

                                    // 최신글의 url을 가져온다.
                                    String nowString1 = String.valueOf(result);
                                    for (int i = 0; nowString1.indexOf("shortcode") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString1.indexOf("shortcode");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString1.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString1.substring(start, end);
                                                    urlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString1 = nowString1.substring(end + 1, nowString1.length());
                                                    break;
                                                }
                                                flag++;

                                                Log.v("섹션", sectionNumber + "");

                                            }
                                        }
                                    }
                                }
                            });
                }
            }
            else if(size == 2){
                if (sectionNumber == 1) {
                    rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                    final String search = m_data[sectionNumber - 1];



                    listView = (ListView) rootView.findViewById(R.id.listView);
                    //callList();
                    imgUrlList = new ArrayList<>();
                    urlList = new ArrayList<>();  /////
                    hashtagList = new ArrayList<>();
                    dataList = new ArrayList<>();


                    Ion.with(this)
                            .load("https://www.instagram.com/explore/tags/" + search + "/?hl=ko")
                            .asString(Charsets.UTF_8) // .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    // 최신글의 이미지를 가져온다.
                                    String nowString = String.valueOf(result);
                                    for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString.indexOf("display_url");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString.substring(start, end);
                                                    imgUrlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString = nowString.substring(end + 1, nowString.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }

                                    // 최신글의 url을 가져온다.
                                    String nowString1 = String.valueOf(result);
                                    for (int i = 0; nowString1.indexOf("shortcode") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString1.indexOf("shortcode");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString1.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString1.substring(start, end);
                                                    urlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString1 = nowString1.substring(end + 1, nowString1.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }

                                    Log.v("donen", imgUrlList.size() + "");

                                    for (int i = 0; i < imgUrlList.size(); i++) {
                                        Data data = new Data(imgUrlList.get(i), urlList.get(i));
                                        dataList.add(data);
                                    }
                                    adapter = new Adapter(getContext(), R.layout.support_simple_spinner_dropdown_item, dataList, search);

                                    listView.setAdapter(adapter);
                                }
                            });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getContext(), DetailActivity.class);
                            intent.putExtra("user", m_user);
                            intent.putExtra("url", imgUrlList.get(position));
                            Toast.makeText(getContext(), "ㅎㅎ", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    });
                } else if (sectionNumber == 2) {
                    rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                    final String search = m_data[sectionNumber - 1];


                    listView = (ListView) rootView.findViewById(R.id.listView);
                    //callList();
                    imgUrlList = new ArrayList<>();
                    urlList = new ArrayList<>();  /////
                    hashtagList = new ArrayList<>();
                    dataList = new ArrayList<>();


                    Ion.with(this)
                            .load("https://www.instagram.com/explore/tags/" + search + "/?hl=ko")
                            .asString(Charsets.UTF_8) // .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    // 최신글의 이미지를 가져온다.
                                    String nowString = String.valueOf(result);
                                    for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString.indexOf("display_url");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString.substring(start, end);
                                                    imgUrlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString = nowString.substring(end + 1, nowString.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }

                                adapter = new Adapter(getContext(), R.layout.support_simple_spinner_dropdown_item, dataList, search);
                                listView.setAdapter(adapter);
                            }
                        });

            } else if (sectionNumber == 2 && m_data.length > 1) {
                rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                TextView title = (TextView) rootView.findViewById(R.id.title);
                final String search = m_data[sectionNumber - 1];


                title.setText(search.toString());

                listView = (ListView) rootView.findViewById(R.id.listView);
                //callList();
                imgUrlList = new ArrayList<>();
                urlList = new ArrayList<>();  /////
                hashtagList = new ArrayList<>();
                dataList = new ArrayList<>();


                Ion.with(this)
                        .load("https://www.instagram.com/explore/tags/" + search + "/?hl=ko")
                        .asString(Charsets.UTF_8) // .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                // 최신글의 이미지를 가져온다.
                                String nowString = String.valueOf(result);
                                for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
                                    int flag = 0;
                                    int start = nowString.indexOf("display_url");
                                    int end = 0;
                                    for (int j = start; ; j++) {
                                        if (nowString.charAt(j) == '\"') {
                                            if (flag == 1) {
                                                start = j + 1;
                                            } else if (flag == 2) {
                                                end = j;
                                                String img = nowString.substring(start, end);
                                                imgUrlList.add(img);
                                                Log.v("asdf", img + "");
                                                nowString = nowString.substring(end + 1, nowString.length());
                                                break;
                                            }
                                        }
                                    }
                                }

                                    // 최신글의 url을 가져온다.
                                    String nowString1 = String.valueOf(result);
                                    for (int i = 0; nowString1.indexOf("shortcode") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString1.indexOf("shortcode");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString1.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString1.substring(start, end);
                                                    urlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString1 = nowString1.substring(end + 1, nowString1.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }

                                    Log.v("donen", imgUrlList.size() + "");

                                    for (int i = 0; i < imgUrlList.size(); i++) {
                                        Data data = new Data(imgUrlList.get(i), urlList.get(i));
                                        dataList.add(data);
                                    }
                                    adapter = new Adapter(getContext(), R.layout.support_simple_spinner_dropdown_item, dataList, search);

                                    listView.setAdapter(adapter);
                                }
                            });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getContext(), DetailActivity.class);
                            intent.putExtra("user", m_user);
                            intent.putExtra("url", imgUrlList.get(position));
                            Toast.makeText(getContext(), "ㅎㅎ", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    });
                }
            }
            else if(size == 3){
                if (sectionNumber == 1) {
                    rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                    final String search = m_data[sectionNumber - 1];



                    listView = (ListView) rootView.findViewById(R.id.listView);
                    //callList();
                    imgUrlList = new ArrayList<>();
                    urlList = new ArrayList<>();  /////
                    hashtagList = new ArrayList<>();
                    dataList = new ArrayList<>();


                    Ion.with(this)
                            .load("https://www.instagram.com/explore/tags/" + search + "/?hl=ko")
                            .asString(Charsets.UTF_8) // .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    // 최신글의 이미지를 가져온다.
                                    String nowString = String.valueOf(result);
                                    for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString.indexOf("display_url");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString.substring(start, end);
                                                    imgUrlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString = nowString.substring(end + 1, nowString.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }
                                adapter = new Adapter(getContext(), R.layout.support_simple_spinner_dropdown_item, dataList, search);
                                listView.setAdapter(adapter);
                            }
                        });

            } else if (sectionNumber == 3 && m_data.length > 2) {
                rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                TextView title = (TextView) rootView.findViewById(R.id.title);
                final String search = m_data[sectionNumber - 1];


                title.setText(search.toString());

                listView = (ListView) rootView.findViewById(R.id.listView);
                //callList();
                imgUrlList = new ArrayList<>();
                urlList = new ArrayList<>();  /////
                hashtagList = new ArrayList<>();
                dataList = new ArrayList<>();


                Ion.with(this)
                        .load("https://www.instagram.com/explore/tags/" + search + "/?hl=ko")
                        .asString(Charsets.UTF_8) // .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                // 최신글의 이미지를 가져온다.
                                String nowString = String.valueOf(result);
                                for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
                                    int flag = 0;
                                    int start = nowString.indexOf("display_url");
                                    int end = 0;
                                    for (int j = start; ; j++) {
                                        if (nowString.charAt(j) == '\"') {
                                            if (flag == 1) {
                                                start = j + 1;
                                            } else if (flag == 2) {
                                                end = j;
                                                String img = nowString.substring(start, end);
                                                imgUrlList.add(img);
                                                Log.v("asdf", img + "");
                                                nowString = nowString.substring(end + 1, nowString.length());
                                                break;
                                            }
                                        }
                                    }
                                }


                                    // 최신글의 url을 가져온다.
                                    String nowString1 = String.valueOf(result);
                                    for (int i = 0; nowString1.indexOf("shortcode") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString1.indexOf("shortcode");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString1.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString1.substring(start, end);
                                                    urlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString1 = nowString1.substring(end + 1, nowString1.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }

                                    Log.v("donen", imgUrlList.size() + "");

                                    for (int i = 0; i < imgUrlList.size(); i++) {
                                        Data data = new Data(imgUrlList.get(i), urlList.get(i));
                                        dataList.add(data);
                                    }
                                    adapter = new Adapter(getContext(), R.layout.support_simple_spinner_dropdown_item, dataList, search);

                                    listView.setAdapter(adapter);
                                }
                            });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getContext(), DetailActivity.class);
                            intent.putExtra("user", m_user);
                            intent.putExtra("url", imgUrlList.get(position));
                            Toast.makeText(getContext(), "ㅎㅎ", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    });
                } else if (sectionNumber == 2) {
                    rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                    final String search = m_data[sectionNumber - 1];


                    listView = (ListView) rootView.findViewById(R.id.listView);
                    //callList();
                    imgUrlList = new ArrayList<>();
                    urlList = new ArrayList<>();  /////
                    hashtagList = new ArrayList<>();
                    dataList = new ArrayList<>();


                    Ion.with(this)
                            .load("https://www.instagram.com/explore/tags/" + search + "/?hl=ko")
                            .asString(Charsets.UTF_8) // .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    // 최신글의 이미지를 가져온다.
                                    String nowString = String.valueOf(result);
                                    for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString.indexOf("display_url");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString.substring(start, end);
                                                    imgUrlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString = nowString.substring(end + 1, nowString.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }

                                    // 최신글의 url을 가져온다.
                                    String nowString1 = String.valueOf(result);
                                    for (int i = 0; nowString1.indexOf("shortcode") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString1.indexOf("shortcode");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString1.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString1.substring(start, end);
                                                    urlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString1 = nowString1.substring(end + 1, nowString1.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }

                                    Log.v("donen", imgUrlList.size() + "");

                                    for (int i = 0; i < imgUrlList.size(); i++) {
                                        Data data = new Data(imgUrlList.get(i), urlList.get(i));
                                        dataList.add(data);
                                    }
                                    adapter = new Adapter(getContext(), R.layout.support_simple_spinner_dropdown_item, dataList, search);

                                    listView.setAdapter(adapter);
                                }
                            });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getContext(), DetailActivity.class);
                            intent.putExtra("user", m_user);
                            intent.putExtra("url", imgUrlList.get(position));
                            Toast.makeText(getContext(), "ㅎㅎ", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    });

                } else if (sectionNumber == 3) {
                    rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                    final String search = m_data[sectionNumber - 1];


                    listView = (ListView) rootView.findViewById(R.id.listView);
                    //callList();
                    imgUrlList = new ArrayList<>();
                    urlList = new ArrayList<>();  /////
                    hashtagList = new ArrayList<>();
                    dataList = new ArrayList<>();


                    Ion.with(this)
                            .load("https://www.instagram.com/explore/tags/" + search + "/?hl=ko")
                            .asString(Charsets.UTF_8) // .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    // 최신글의 이미지를 가져온다.
                                    String nowString = String.valueOf(result);
                                    for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString.indexOf("display_url");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString.substring(start, end);
                                                    imgUrlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString = nowString.substring(end + 1, nowString.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }

                                    // 최신글의 url을 가져온다.
                                    String nowString1 = String.valueOf(result);
                                    for (int i = 0; nowString1.indexOf("shortcode") != -1; i++) {
                                        int flag = 0;
                                        int start = nowString1.indexOf("shortcode");
                                        int end = 0;
                                        for (int j = start; ; j++) {
                                            if (nowString1.charAt(j) == '\"') {
                                                if (flag == 1) {
                                                    start = j + 1;
                                                } else if (flag == 2) {
                                                    end = j;
                                                    String img = nowString1.substring(start, end);
                                                    urlList.add(img);
                                                    Log.v("asdf", img + "");
                                                    nowString1 = nowString1.substring(end + 1, nowString1.length());
                                                    break;
                                                }
                                                flag++;
                                            }
                                        }
                                    }

                                    Log.v("donen", imgUrlList.size() + "");

                                    for (int i = 0; i < imgUrlList.size(); i++) {
                                        Data data = new Data(imgUrlList.get(i), urlList.get(i));
                                        dataList.add(data);
                                    }
                                    adapter = new Adapter(getContext(), R.layout.support_simple_spinner_dropdown_item, dataList, search);

                                    listView.setAdapter(adapter);
                                }

                            });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getContext(), DetailActivity.class);
                            intent.putExtra("user", m_user);
                            intent.putExtra("url", imgUrlList.get(position));
                            Toast.makeText(getContext(), "ㅎㅎ", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    });
                }
            } else if (sectionNumber == m_data.length + 1) {
                rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                TextView title = (TextView) rootView.findViewById(R.id.title);
                title.setText("집 앞 카페");


                // GPSTracker class
                GpsInfo gps;


                nearCafeList = (ListView) rootView.findViewById(R.id.listView);

                nearCafeDataList = new ArrayList<>();

                name = new ArrayList<>();
                id = new ArrayList<>();
                imgList = new ArrayList<>();

                if (!isPermission) {
                    callPermission();
                    //return;
                }

//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(getContext(), DetailActivity.class);
//                    intent.putExtra("user", m_user);
//                    Toast.makeText(getContext(), "ㅎㅎ", Toast.LENGTH_SHORT).show();
//                    startActivity(intent);
//                }
//            });
                gps = new GpsInfo(getContext());
                // GPS 사용유무 가져오기
                double latitude = 0.0;
                double longitude = 0.0;
                if (gps.isGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    Toast.makeText(getContext(), "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }

                String x = longitude + "";
                String y = latitude + "";


                Ion.with(this)
                        .load("https://m.store.naver.com/places/listMap?display=40&level=middle&nlu=%5Bobject%20Object%5D&query=%EC%B9%B4%ED%8E%98&sid=468329393%2C37392371%2C967978358&sortingOrder=distance&viewType=place&x=" + x + "&y=" + y)
                        .asString(Charsets.UTF_8) // .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                String nowString = result;


                                // 상호명 가져오기
                                for (int i = 0; nowString.indexOf("name\":\"") != -1; i++) {
                                    int flag = 0;
                                    int start = nowString.indexOf("name\":\"");
                                    int end = 0;
                                    for (int j = start; ; j++) {
                                        if (nowString.charAt(j) == '\"') {
                                            if (flag == 1) {
                                                start = j + 1;
                                            } else if (flag == 2) {
                                                end = j;
                                                String img = nowString.substring(start, end);
                                                name.add(img);
                                                Log.v("done2", img + "");
                                                nowString = nowString.substring(end + 1, nowString.length());
                                                break;
                                            }
                                            flag++;
                                        }
                                    }
                                }

                                // 상호에 대한 아이디
                                String nowString1 = result;
                                for (int i = 0; nowString1.indexOf("id\":\"") != -1; i++) {
                                    int flag = 0;
                                    int start = nowString1.indexOf("id\":\"");
                                    int end = 0;
                                    for (int j = start; ; j++) {
                                        if (nowString1.charAt(j) == '\"') {
                                            if (flag == 1) {
                                                start = j + 1;
                                            } else if (flag == 2) {
                                                end = j;
                                                String img = nowString1.substring(start, end);
                                                id.add(img);
                                                Log.v("done2", img + "");
                                                nowString1 = nowString1.substring(end + 1, nowString1.length());
                                                break;
                                            }
                                            flag++;
                                        }
                                    }
                                }


                                // 비동기 문제로 안에서 처리해준다
                                Log.v("donen", name.size() + " " + id.size());
                                for (int i = 0; i < id.size(); i++) {

                                    // 이미지 가져오기
                                    final int finalI = i;

                                    Ion.with(getContext())
                                            .load("https://www.instagram.com/explore/tags/" + name.get(i) + "/?hl=ko")
                                            .asString(Charsets.UTF_8) // .asString()
                                            .setCallback(new FutureCallback<String>() {
                                                @Override
                                                public void onCompleted(Exception e, String result) {
                                                    String nowString = result;
                                                    int flag = 0;
                                                    int start = nowString.indexOf("display_url");
                                                    int end = 0;
                                                    for (int j = start; start != -1; j++) {
                                                        if (nowString.charAt(j) == '\"') {
                                                            if (flag == 1) {
                                                                start = j + 1;
                                                            } else if (flag == 2) {
                                                                end = j;
                                                                String img = nowString.substring(start, end);
                                                                NearCafeData nearCafeData = new NearCafeData(name.get(finalI), id.get(finalI), img);
                                                                nearCafeDataList.add(nearCafeData);
                                                                Log.v("asdf", img + "");
                                                                nowString = nowString.substring(end + 1, nowString.length());
                                                                break;
                                                            }
                                                            flag++;
                                                        }
                                                    }

                                                    Log.v("사이즈", nearCafeDataList + "");
                                                    nearCafeAdapter = new NearCafeAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, nearCafeDataList);
                                                    nearCafeList.setAdapter(nearCafeAdapter);
                                                }
                                            });
                                }
                            }
                        });
            }
            return rootView;
        }




        private void callList(LayoutInflater inflater, ViewGroup container, int sectionNumber) {


            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            TextView title = (TextView) rootView.findViewById(R.id.title);
            final String search = m_data[sectionNumber - 1];


            title.setText(search.toString());

            listView = (ListView) rootView.findViewById(R.id.listView);
            //callList();
            imgUrlList = new ArrayList<>();
            urlList = new ArrayList<>();  /////
            hashtagList = new ArrayList<>();
            dataList = new ArrayList<>();


            Ion.with(this)
                    .load("https://www.instagram.com/explore/tags/" + search + "/?hl=ko")
                    .asString(Charsets.UTF_8) // .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            // 최신글의 이미지를 가져온다.
                            String nowString = String.valueOf(result);
                            for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
                                int flag = 0;
                                int start = nowString.indexOf("display_url");
                                int end = 0;
                                for (int j = start; ; j++) {
                                    if (nowString.charAt(j) == '\"') {
                                        if (flag == 1) {
                                            start = j + 1;
                                        } else if (flag == 2) {
                                            end = j;
                                            String img = nowString.substring(start, end);
                                            imgUrlList.add(img);
                                            Log.v("asdf", img + "");
                                            nowString = nowString.substring(end + 1, nowString.length());
                                            break;
                                        }
                                        flag++;
                                    }
                                }
                            }

                            // 최신글의 url을 가져온다.
                            String nowString1 = String.valueOf(result);
                            for (int i = 0; nowString1.indexOf("shortcode") != -1; i++) {
                                int flag = 0;
                                int start = nowString1.indexOf("shortcode");
                                int end = 0;
                                for (int j = start; ; j++) {
                                    if (nowString1.charAt(j) == '\"') {
                                        if (flag == 1) {
                                            start = j + 1;
                                        } else if (flag == 2) {
                                            end = j;
                                            String img = nowString1.substring(start, end);
                                            urlList.add(img);
                                            Log.v("asdf", img + "");
                                            nowString1 = nowString1.substring(end + 1, nowString1.length());
                                            break;
                                        }
                                        flag++;
                                    }
                                }
                            }

                            Log.v("donen", imgUrlList.size() + "");

                            for (int i = 0; i < imgUrlList.size(); i++) {
                                Data data = new Data(imgUrlList.get(i), urlList.get(i));
                                dataList.add(data);
                            }
                            adapter = new Adapter(getContext(), R.layout.support_simple_spinner_dropdown_item, dataList, search);
                            listView.setAdapter(adapter);
                        }
                    });

        }


        //권한요청
        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                               int[] grantResults) {
            if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                isAccessFineLocation = true;

            } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                isAccessCoarseLocation = true;
            }

            if (isAccessFineLocation && isAccessCoarseLocation) {
                isPermission = true;
            }
        }

        // 전화번호 권한 요청
        private void callPermission() {
            // Check the SDK version and whether the permission is already granted or not.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && getContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_ACCESS_FINE_LOCATION);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && getContext().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                isPermission = true;
            }
        }

        public void makeFile() {
            String hash = "";
            Scanner scan = new Scanner(getResources().openRawResource(R.raw.hash));
            while (scan.hasNextLine()) {
                hash += scan.nextLine();
            }
            hashtag1 = hash.split(" ");
            //Toast.makeText(this, hashtag[1], Toast.LENGTH_SHORT).show();;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return m_prefSize + 1;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (m_prefSize == 1) {
                switch (position) {
                    case 0:
                        return m_data[0];
                    case 1:
                        return "집 앞 카페";
                }
            } else if (m_prefSize == 2) {
                switch (position) {
                    case 0:
                        return m_data[0];
                    case 1:
                        return m_data[1];
                    case 2:
                        return "집 앞 카페";
                }
            } else if (m_prefSize == 3) {
                switch (position) {
                    case 0:
                        return m_data[0];
                    case 1:
                        return m_data[1];
                    case 2:
                        return m_data[2];
                    case 3:
                        return "집 앞 카페";
                }
            }
            return null;
        }
    }


}
