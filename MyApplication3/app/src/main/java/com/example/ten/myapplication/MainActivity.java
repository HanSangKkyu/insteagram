package com.example.ten.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SlidingView sv = new SlidingView(this);
        View v1 = View.inflate(this, R.layout.activity_main, null);
        View v2 = View.inflate(this, R.layout.layout2, null);
        sv.addView(v1);
        sv.addView(v2);
        setContentView(sv);

    }

    public void ClickBtn(View view) {
        switch (view.getId()) {
            case R.id.joinBtn:
                //혜진
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.ApiBtn:
                //혜지&상규
                startActivity(new Intent(this, InstargramAPI.class));
                break;
            case R.id.MapBtn:
                Intent intent = new Intent(this, ShowMapActivity.class);
                startActivity(intent);
                //열
                break;
            default:
                break;
        }
    }
}
