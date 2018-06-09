package com.example.ten.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.util.Charsets;
import com.koushikdutta.ion.Ion;

public class JsonParsing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_parsing);


        Ion.with(this)
                .load("https://open-korean-text.herokuapp.com/extractPhrases?text=%EC%98%A4%ED%94%88%EC%BD%94%EB%A6%AC%EC%95%88%ED%85%8D%EC%8A%A4%ED%8A%B8")
                .asJsonObject(Charsets.UTF_8)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray array = result.getAsJsonArray("phrases");
                        for (int i = 0; i < array.size(); i++) {
                            Log.v("제이썬", array.get(i) + "");
                        }
                    }
                });
    }
}
