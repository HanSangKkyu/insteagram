package com.example.ten.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    EditText editID;
    EditText editPW;
    Button loginButton;

    String inputID;
    String inputPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    public void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        editID = (EditText) findViewById(R.id.inputID);
        editPW = (EditText) findViewById(R.id.inputPW);

        loginButton = (Button) findViewById(R.id.loginButton);
    }


    public void instaLogin(View view) {
    }

    public void joinBtn(View view) {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        inputID = editID.getText().toString();
        inputPW = editPW.getText().toString();

        Toast.makeText(this, inputID+", "+inputPW, Toast.LENGTH_SHORT).show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while(child.hasNext()) {
                    if(child.next().getKey().equals(inputID)) {
                        // 아이디 존재
                        String userPW = dataSnapshot.child(inputID).child("pw").getValue().toString();

                        if(inputPW.equals(userPW)) {
                            // 비밀번호 똑같아
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                            // 성공하면 어디로 가야함
                            /// 여기다가 하세여
                        }
                        else {
                            // 비밀번호 틀렸어
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            editID.setText("");
                            editPW.setText("");
                            return;
                        }
                    }
                }
                Toast.makeText(LoginActivity.this, "로그인 정보를 확인하세요.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
