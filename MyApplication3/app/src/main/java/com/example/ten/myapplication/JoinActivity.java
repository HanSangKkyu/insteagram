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
    boolean[] check;
    int count; // 속성 몇개 선택했는지 저장할 것

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
        check = new boolean[9]; // 9개의 선호 카페가 체크되었는지 저장하는 배열
        count = 0;

        for(int i=0; i<check.length; i++)
            check[i] = false; // false로 초기화, 선택되면 true

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

        String pref = "";
        if(id != null && pw != null && count != 0) {
            flag = true;

            for(int i=0; i<9; i++) {
                if(check[i]) {
                    switch(i) {
                        case 0:
                            pref += "플라워카페 ";
                            break;
                        case 1:
                            pref += "동물카페 ";
                            break;
                        case 2:
                            pref += "키즈카페 ";
                            break;
                        case 3:
                            pref += "루프탑카페 ";
                            break;
                        case 4:
                            pref += "캐릭터카페 ";
                            break;
                        case 5:
                            pref += "저렴한카페 ";
                            break;
                        case 6:
                            pref += "사주카페 ";
                            break;
                        case 7:
                            pref += "북카페 ";
                            break;
                        case 8:
                            pref += "디저트카페 ";
                            break;
                    }
                }
            }
        }
        else {
            Toast.makeText(this, "입력 정보를 확인하세요.", Toast.LENGTH_SHORT).show();
        }

        if(flag) {
            User user = new User(id, pw, pref);
            databaseReference.child(id).setValue(user);
            Toast.makeText(this, "회원 가입 완료", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            //intent.putExtra("user", user);
            startActivity(intent);

//            Intent i = new Intent(this, InstargramAPI.class);
//            i.putExtra("pref", pref);
//            startActivity(i);
        }
    }

    public void checkPreference(View view) {

        // 지금은 버튼 색깔 바꾸는데 나중에는 이미지로 바꾸면 될 것 같아요!

        switch(view.getId()) {
            case R.id.flower: // 플라워 카페
                if(check[0]) { // 체크 되었으면
                    check[0] = false; // 체크 풀기
                    count--;
                    ((Button) findViewById(R.id.flower)).setBackgroundResource(R.drawable.btnnormal);
                }
                else { // 체크 안 되었으면
                    if(count < 3) { // 현재 세개 미만 선택되었을 때만
                        check[0] = true; // 체크하기
                        count++;
                        ((Button) findViewById(R.id.flower)).setBackgroundResource(R.drawable.btnclick);
                    }
                    else
                        Toast.makeText(this, "3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.cat: // 동물 카페
                if(check[1]) { // 체크 되었으면
                    check[1] = false; // 체크 풀기
                    count--;
                    ((Button) findViewById(R.id.cat)).setBackgroundResource(R.drawable.btnnormal);
                }
                else { // 체크 안 되었으면
                    if(count < 3) {
                        check[1] = true; // 체크하기
                        count++;
                        ((Button) findViewById(R.id.cat)).setBackgroundResource(R.drawable.btnclick);
                    }
                    else
                        Toast.makeText(this, "3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.dog: // 만화 카페
                if(check[2]) { // 체크 되었으면
                    check[2] = false; // 체크 풀기
                    count--;
                    ((Button) findViewById(R.id.dog)).setBackgroundResource(R.drawable.btnnormal);
                }
                else { // 체크 안 되었으면
                    if(count < 3) {
                        check[2] = true; // 체크하기
                        count++;
                        ((Button) findViewById(R.id.dog)).setBackgroundResource(R.drawable.btnclick);
                    }
                    else
                        Toast.makeText(this, "3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.roofTop: // 루프탑 카페
                if(check[3]) { // 체크 되었으면
                    check[3] = false; // 체크 풀기
                    count--;
                    ((Button) findViewById(R.id.roofTop)).setBackgroundResource(R.drawable.btnnormal);
                }
                else { // 체크 안 되었으면
                    if(count < 3) {
                        check[3] = true; // 체크하기
                        count++;
                        ((Button) findViewById(R.id.roofTop)).setBackgroundResource(R.drawable.btnclick);
                    }
                    else
                        Toast.makeText(this, "3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.character: // 캐릭터 카페
                if(check[4]) { // 체크 되었으면
                    check[4] = false; // 체크 풀기
                    count--;
                    ((Button) findViewById(R.id.character)).setBackgroundResource(R.drawable.btnnormal);
                }
                else { // 체크 안 되었으면
                    if(count < 3) {
                        check[4] = true; // 체크하기
                        count++;
                        ((Button) findViewById(R.id.character)).setBackgroundResource(R.drawable.btnclick);
                    }
                    else
                        Toast.makeText(this, "3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.cheap: // 저렴한 카페
                if(check[5]) { // 체크 되었으면
                    check[5] = false; // 체크 풀기
                    count--;
                    ((Button) findViewById(R.id.cheap)).setBackgroundResource(R.drawable.btnnormal);
                }
                else { // 체크 안 되었으면
                    if(count < 3) {
                        check[5] = true; // 체크하기
                        count++;
                        ((Button) findViewById(R.id.cheap)).setBackgroundResource(R.drawable.btnclick);
                    }
                    else
                        Toast.makeText(this, "3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.fortune: // 사주 카페
                if(check[6]) { // 체크 되었으면
                    check[6] = false; // 체크 풀기
                    count--;
                    ((Button) findViewById(R.id.fortune)).setBackgroundResource(R.drawable.btnnormal);
                }
                else { // 체크 안 되었으면
                    if(count < 3) {
                        check[6] = true; // 체크하기
                        count++;
                        ((Button) findViewById(R.id.fortune)).setBackgroundResource(R.drawable.btnclick);
                    }
                    else
                        Toast.makeText(this, "3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.book: // 북 카페
                if(check[7]) { // 체크 되었으면
                    check[7] = false; // 체크 풀기
                    count--;
                    ((Button) findViewById(R.id.book)).setBackgroundResource(R.drawable.btnnormal);
                }
                else { // 체크 안 되었으면
                    if(count < 3) {
                        check[7] = true; // 체크하기
                        count++;
                        ((Button) findViewById(R.id.book)).setBackgroundResource(R.drawable.btnclick);
                    }
                    else
                        Toast.makeText(this, "3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.dessert: // 디저트 카페
                if(check[8]) { // 체크 되었으면
                    check[8] = false; // 체크 풀기
                    count--;
                    ((Button) findViewById(R.id.dessert)).setBackgroundResource(R.drawable.btnnormal);
                }
                else { // 체크 안 되었으면
                    if(count < 3) {
                        check[8] = true; // 체크하기
                        count++;
                        ((Button) findViewById(R.id.dessert)).setBackgroundResource(R.drawable.btnclick);
                    }
                    else
                        Toast.makeText(this, "3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
