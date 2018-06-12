package com.example.ten.myapplication;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class GeoCodeActivity extends AppCompatActivity {

    EditText address;
    Geocoder geocoder;
    List<Address> result;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_code);

        init();
    }

    public void init() {
        address = (EditText) findViewById(R.id.address);
        geocoder = new Geocoder(this);
        textView = (TextView) findViewById(R.id.textView);
    }

    public void addressToLatLng(View view) {
        String str = address.getText().toString();
        try {
            result = geocoder.getFromLocationName(str, 1);
            Address address = result.get(0);
            textView.setText("위도: "+address.getLatitude()+"\n경도: "+address.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
