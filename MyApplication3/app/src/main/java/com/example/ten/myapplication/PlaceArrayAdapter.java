package com.example.ten.myapplication;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import static com.example.ten.myapplication.Adapter.mData;

public class PlaceArrayAdapter extends ArrayAdapter<PlaceArrayAdapter.PlaceAutocomplete> {
    Context context;
    private static final String TAG = "PlaceArrayAdapter";
    GoogleApiClient mGoogleApiClient;
    private AutocompleteFilter mPlaceFilter;
    private LatLngBounds mBounds;
    private ArrayList<PlaceAutocomplete> mResultList;
    int globalPositon = 0;

    /**
     * Constructor
     *
     * @param context  Context
     * @param resource Layout resource
     * @param bounds   Used to specify the search bounds
     * @param filter   Used to specify place types
     */
    public PlaceArrayAdapter(Context context, int resource, LatLngBounds bounds,
                             AutocompleteFilter filter) {
        super(context, resource);
        this.context = context;
        mBounds = bounds;
        mPlaceFilter = filter;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            mGoogleApiClient = null;
        } else {
            mGoogleApiClient = googleApiClient;
        }
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    public ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint, String place, int position) {
        if (mGoogleApiClient != null) {
            Log.i(TAG, "Executing autocomplete query for: " + constraint);
            String str = constraint.toString();
            //String[] s = str.split("#");
            Status status = null;
            AutocompletePredictionBuffer autocompletePredictions = null;

            ArrayList resultList;
            Log.v("태그들정답", place + " " + position);
//            do {
            resultList = null;
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, str + " " + place,
                                    mBounds, mPlaceFilter);
            results.setResultCallback(mUpdatePlaceDetailsCallback);
            globalPositon = position;
            return resultList;
//        }
            //  Log.e(TAG, "Google API client is not connected.");
            // return null;
        }
        return null;
    }

    // 장소불러오기 성공 실패 -> 성공일 경우 검색한 장소에대한 정보를 가지고 있음
    private ResultCallback<AutocompletePredictionBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<AutocompletePredictionBuffer>() {
        @Override
        public void onResult(AutocompletePredictionBuffer places) {
            AutocompletePredictionBuffer autocompletePredictions = null;
            ArrayList resultList;
            if (!places.getStatus().isSuccess()) {
                // 실패
                places.release();
                Log.v("태그들3", "실패");
                return;
            }

            // 장소 이름     : place.getName()
            // 장소 ID       : place.getId()
            // 장소 주소     : place.getAddress()
            // 장소 전화번호  : place.getPhoneNumber()
            // 장소 url      : place.getWebsiteUri()

            // 성공
            autocompletePredictions = places;
            //places.
            //final Place place = places.get(0);

            // 장소에 대한 GPS 정보
            //queriedLocation = place.getLatLng();
            //locationName = "" + place.getName();

            // 장소 클릭했을 때 뜨는 위치 명
//            Log.i("검색한 위치명: ", place.getName()+"");
            String str = "";
            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                        prediction.getFullText(null)));
                str = prediction.getFullText(null).toString();
                break;
            }
            Log.v("태그들3", str);
            if (str.indexOf("대한민국") != -1) {
                int idx = str.indexOf("시");
                String n = str.substring(idx + 1);
                str += "#" + n;
                Message message = Message.obtain(Adapter.handler, 0, str);

                Log.v("태그들기모찌", str + " " + globalPositon);
                String[] temp = str.split("#");
                mData.get(globalPositon).setAddress(temp[0]);
                if (temp.length == 1)
                    mData.get(globalPositon).setName(temp[0]);
                else
                    mData.get(globalPositon).setName(temp[1]);


//                Adapter.handler.sendMessage(message);
            }


//            Toast.makeText(getContext(), "장소검사 :"+str,Toast.LENGTH_SHORT ).show();
            //resultList.get(0);
            // Buffer release
            autocompletePredictions.release();

            places.release();


        }
    };

    //
    class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }

    public String Filtering1() {
        Scanner scan = new Scanner(context.getResources().openRawResource(R.raw.location));
        String str = "";
        while (scan.hasNextLine()) {
            str += scan.nextLine();
        }
        String[] s = str.split("#");
        String str2 = "#하나코히카페#하나코히플라워#울산카페#카페#일상#데일리#좋아요#소통#인스타그램#감성#선팔#맞팔#셀스타그램#daily#selfie#lfl#f4f#ootd#핫플#오오티디#취향저격#울산#플라워카페#셀카#셀피#얼스타그램#포토존#토요일";

        String[] s2 = str2.split("#");
        for (int i = 0; i < s2.length; i++) {
            for (int j = 0; j < s.length; j++) {
                if (s2[i].equals(s[j]))
                    return s2[i];
            }
        }
        return "";
    }


}