package com.example.ten.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ChangePrefActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    Button joinButton;
    User user;
    Intent intent;

    String id;
    String pw;
    String pref;

    boolean flag;
    boolean[] check;
    int count; // 속성 몇개 선택했는지 저장할 것
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pref);
        init();
    }

    public void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        joinButton = (Button) findViewById(R.id.submit);
        check = new boolean[9]; // 9개의 선호 카페가 체크되었는지 저장하는 배열
        count = 0;

        for(int i=0; i<check.length; i++)
            check[i] = false; // false로 초기화, 선택되면 true

        intent = getIntent();
        user = (User) intent.getSerializableExtra("users");

        id = user.getId().toString();
        pw = user.getPw().toString();

        flag = false;
    }


    public void join(View view) {
        Toast.makeText(this, pref, Toast.LENGTH_SHORT).show();
        databaseReference.child(user.getId().toString()).child("preferences").setValue(pref);

        if(flag) {
            user.setPreferences(pref);
            databaseReference.child(id).child("preferences").setValue(pref);
            Toast.makeText(this, "정보 변경 완료", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, Main2Activity.class);
            i.putExtra("user", user);
            startActivity(i);

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

            case R.id.cat: // 고양이 카페
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

            case R.id.dog: // 강아지 카페
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
        pref = "";
        if(id != null && pw != null && count != 0) {
            flag = true;

            for(int i=0; i<9; i++) {
                if(check[i]) {
                    switch(i) {
                        case 0:
                            pref += "플라워카페 ";
                            break;
                        case 1:
                            pref += "고양이카페 ";
                            break;
                        case 2:
                            pref += "강아지카페 ";
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
    }
}
