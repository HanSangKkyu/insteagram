package com.example.ten.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class PlaceArrayAdapter
        extends ArrayAdapter<PlaceArrayAdapter.PlaceAutocomplete> implements Filterable {
    Context context;
    private static final String TAG = "PlaceArrayAdapter";
    private GoogleApiClient mGoogleApiClient;
    private AutocompleteFilter mPlaceFilter;
    private LatLngBounds mBounds;
    private ArrayList<PlaceAutocomplete> mResultList;

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
        this.context=context;
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

    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {
        if (mGoogleApiClient != null) {
            Log.i(TAG, "Executing autocomplete query for: " + constraint);
            String str = "하나코히#하나코히플라워#lfl#f4f#ootd#핫플#오오티디#취향저격#플라워카페#포토존#토요일";
            String[] s=str.split("#");
            Status status=null;
            AutocompletePredictionBuffer autocompletePredictions=null;
            int count=0;
            ArrayList resultList;

            do {
                resultList=null;
                PendingResult<AutocompletePredictionBuffer> results =
                        Places.GeoDataApi
                                .getAutocompletePredictions(mGoogleApiClient, s[count]+ " 울산",
                                        mBounds, mPlaceFilter);
                // Wait for predictions, set the timeout.
                autocompletePredictions = results
                        .await(60, TimeUnit.SECONDS);
                status = autocompletePredictions.getStatus();
                count++;
                if (count == s.length) {
                    Log.i("Status", "장소가 아니거나 해당 장소를 찾을 수 없음.");
                }

                if (!status.isSuccess()) {
                    Toast.makeText(getContext(), "Error: " + status.toString(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error getting place predictions: " + status
                            .toString());
                    autocompletePredictions.release();
                    return null;
                }

                Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                        + " predictions.");
                Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                resultList = new ArrayList<>(autocompletePredictions.getCount());
                while (iterator.hasNext()) {
                    AutocompletePrediction prediction = iterator.next();
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                            prediction.getFullText(null)));
                }
                // Buffer release
                autocompletePredictions.release();
            }while(resultList.size()==0);

            return resultList;
        }
        Log.e(TAG, "Google API client is not connected.");
        return null;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    // Query the autocomplete API for the entered constraint
                    mResultList = getPredictions(constraint);
                    if (mResultList != null) {
                        // Results
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

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
    public void makeFile(){
        String hash="";
        Scanner scan=new Scanner(context.getResources().openRawResource(R.raw.hash));
        while(scan.hasNextLine()){
            hash+=scan.nextLine();
        }
        //hashtag=hash.split(" ");
        //Toast.makeText(this, hashtag[1], Toast.LENGTH_SHORT).show();;
    }
}