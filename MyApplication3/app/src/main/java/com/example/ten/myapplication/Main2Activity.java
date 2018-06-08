package com.example.ten.myapplication;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


public class Main2Activity extends AppCompatActivity {
    int m_prefSize; //관심사 개수
    static String[] m_data; //관심사 저장한 배열
    String m_pref;
    DatabaseReference rDatabase;


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
        Intent intent = getIntent();
        final User user = (User) intent.getSerializableExtra("user");
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


        ArrayList<String> imgUrlList;
        ArrayList<String> urlList;
        ArrayList<String> hashtagList;
        ArrayList<Data> dataList;
        ListView lsitView;
        Adapter adapter;
        static String[] hashtag1;


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


            if (sectionNumber == 1) {
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

            } else if (sectionNumber == 2) {
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

            } else if (sectionNumber == 3) {
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

            return rootView;
        }


//        private void callList() {
//            imgUrlList = new ArrayList<>();
//            urlList = new ArrayList<>();  /////
//            hashtagList = new ArrayList<>();
//            dataList = new ArrayList<>();
//
//            Ion.with(this)
//                    .load("https://www.instagram.com/explore/tags/%ED%94%8C%EB%9D%BC%EC%9B%8C%EC%B9%B4%ED%8E%98/?hl=ko")
//                    .asString(Charsets.UTF_8) // .asString()
//                    .setCallback(new FutureCallback<String>() {
//                        @Override
//                        public void onCompleted(Exception e, String result) {
//                            // 최신글의 이미지를 가져온다.
//                            String nowString = String.valueOf(result);
//                            for (int i = 0; nowString.indexOf("display_url") != -1; i++) {
//                                int flag = 0;
//                                int start = nowString.indexOf("display_url");
//                                int end = 0;
//                                for (int j = start; ; j++) {
//                                    if (nowString.charAt(j) == '\"') {
//                                        if (flag == 1) {
//                                            start = j + 1;
//                                        } else if (flag == 2) {
//                                            end = j;
//                                            String img = nowString.substring(start, end);
//                                            imgUrlList.add(img);
//                                            Log.v("asdf", img + "");
//                                            nowString = nowString.substring(end + 1, nowString.length());
//                                            break;
//                                        }
//                                        flag++;
//                                    }
//                                }
//                            }
//
//                            // 최신글의 url을 가져온다.
//                            String nowString1 = String.valueOf(result);
//                            for (int i = 0; nowString1.indexOf("shortcode") != -1; i++) {
//                                int flag = 0;
//                                int start = nowString1.indexOf("shortcode");
//                                int end = 0;
//                                for (int j = start; ; j++) {
//                                    if (nowString1.charAt(j) == '\"') {
//                                        if (flag == 1) {
//                                            start = j + 1;
//                                        } else if (flag == 2) {
//                                            end = j;
//                                            String img = nowString1.substring(start, end);
//                                            urlList.add(img);
//                                            Log.v("asdf", img + "");
//                                            nowString1 = nowString1.substring(end + 1, nowString1.length());
//                                            break;
//                                        }
//                                        flag++;
//                                    }
//                                }
//                            }
//
//                            Log.v("donen", imgUrlList.size() + "");
//
//                            for (int i = 0; i < imgUrlList.size(); i++) {
//                                Data data = new Data(imgUrlList.get(i), urlList.get(i));
//                                dataList.add(data);
//                            }
//                            adapter = new Adapter(getContext(), R.layout.support_simple_spinner_dropdown_item, dataList);
//                            lsitView.setAdapter(adapter);
//                        }
//                    });
//        }

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
            return m_prefSize;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (m_prefSize == 1) {
                switch (position) {
                    case 0:
                        return m_data[0];
                }
            } else if (m_prefSize == 2) {
                switch (position) {
                    case 0:
                        return m_data[0];
                    case 1:
                        return m_data[1];
                }
            } else if (m_prefSize == 3) {
                switch (position) {
                    case 0:
                        return m_data[0];
                    case 1:
                        return m_data[1];
                    case 2:
                        return m_data[2];
                }
            }
            return null;
        }
    }


}
