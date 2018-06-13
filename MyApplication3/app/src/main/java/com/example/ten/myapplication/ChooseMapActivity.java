package com.example.ten.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ChooseMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_map);
    }

    public void mapbtnClick(View view) {
        Intent i = getIntent();
        User user = (User)i.getSerializableExtra("user");

        view.getId(); 

        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
