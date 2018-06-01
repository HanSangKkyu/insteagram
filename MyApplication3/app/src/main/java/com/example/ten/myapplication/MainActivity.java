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

    }

    public void ClickBtn(View view) {
        switch (view.getId()) {
            case R.id.joinBtn:
                //혜진
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
