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

public class JoinActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    EditText editID;
    EditText editPW;
    Button checkButton;
    Button joinButton;

    String id;
    String pw;

    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        init();
    }

    public void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        editID = (EditText) findViewById(R.id.inputID);
        editPW = (EditText) findViewById(R.id.inputPW);
        checkButton = (Button) findViewById(R.id.checkButton);
        joinButton = (Button) findViewById(R.id.joinButton);

        id = null;
        pw = null;

        flag = false;
    }

    public void checkID(View view) {

        // 아이디 가져오기
        id = editID.getText().toString();

        // 중복 확인
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while(child.hasNext()) {
                    if(child.next().getKey().equals(id)) {
                        Toast.makeText(JoinActivity.this, "중복되는 ID입니다.", Toast.LENGTH_SHORT).show();
                        editID.setText("");
                        id = null;
                        flag = true;
                        databaseReference.removeEventListener(this);
                        return;
                    }
                }
                Toast.makeText(JoinActivity.this, "사용할 수 있는 ID입니다.", Toast.LENGTH_SHORT).show();
                editID.setEnabled(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void join(View view) {
        pw = editPW.getText().toString();

        if(id != null && pw != null)
            flag = true;
        else {
            Toast.makeText(this, "입력 정보를 확인하세요.", Toast.LENGTH_SHORT).show();
        }

        if(flag) {
            User user = new User(id, pw);
            databaseReference.child(id).setValue(user);
            Toast.makeText(this, "회원 가입 완료", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
