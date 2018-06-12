package com.example.ten.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    DatabaseReference databaseReference_f;
    EditText editID;
    EditText editPW;
    Button loginButton;

    String inputID;
    String inputPW;

    String facebook_id;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    public void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference_f = FirebaseDatabase.getInstance().getReference("users");

        editID = (EditText) findViewById(R.id.inputID);
        editPW = (EditText) findViewById(R.id.inputPW);

        loginButton = (Button) findViewById(R.id.loginButton);
    }


    public void instaLogin(View v) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult result) {

                GraphRequest request;
                request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        if (response.getError() != null) {

                        } else {
                            Log.i("TAG", "user: " + user.toString());
                            Log.i("TAG", "AccessToken: " + result.getAccessToken().getToken());
                            setResult(RESULT_OK);
                            // 로그인 성공시 할 일 아래에 쓰면 됨

                            facebook_id = "";

                            try {
                                // 페이스북 로그인 이메일 가져온다.
                                facebook_id = user.getString("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(LoginActivity.this, facebook_id, Toast.LENGTH_SHORT).show();


                            // Firebase에 저장된 아이디 (이메일)인지 확인한다
//                            databaseReference_f.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
//
//                                    while (child.hasNext()) {
//                                        if (child.next().getKey().equals(facebook_id)) {
//                                            // 이미 존재하는 페이스북 계정이면
//                                            // curUser 정보 보내주고 액티비티 실행
//                                            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
//                                            intent.putExtra("user", facebook_id);
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                        else continue;
//                                    }
//                                    // 존재하지 않는 이메일이라면 가입 절차를 거쳐야함
//                                    Intent intent = new Intent(getApplicationContext(), FacebookJoinActivity.class);
//                                    intent.putExtra("user", facebook_id);
//                                    startActivity(intent);
//                                    finish();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });

                            // 있으면
                            // curUser = 이메일 로 설정하고
                            // Main2Activity 로 넘어가

                            // 없으면
                            // 선호 카페 선택하는 페이지 (만들어야 함)로 이동해서
                            // 선택하게 한 다음에 정보를 Firebase에 저장해야겠네요.
                            // 그리고 그 다음에 Main2Activity로 이동하기

                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        User m_user = snapshot.getValue(User.class);

                                        if(m_user.getId().equals(facebook_id)) {
                                            // 이미 존재하는 페이스북 계정이면
                                            // curUser 정보 보내주고 액티비티 실행
                                            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                                            intent.putExtra("user", m_user);
                                            startActivity(intent);
                                            return;
                                        }
                                        else continue;
                                    }
                                    // 존재하지 않는 이메일이라면 가입 절차를 거쳐야함
                                    Intent intent = new Intent(getApplicationContext(), FacebookJoinActivity.class);
                                    intent.putExtra("user", facebook_id);
                                    startActivity(intent);
                                    return;
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("test", "Error: " + error);
                //finish();
            }

            @Override
            public void onCancel() {
                //finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void joinBtn(View view) {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        inputID = editID.getText().toString();
        inputPW = editPW.getText().toString();

        Toast.makeText(this, inputID + ", " + inputPW, Toast.LENGTH_SHORT).show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while (child.hasNext()) {
                    if (child.next().getKey().equals(inputID)) {
                        // 아이디 존재
                        String userPW = dataSnapshot.child(inputID).child("pw").getValue().toString();

                        if (inputPW.equals(userPW)) {
                            // 비밀번호 똑같아
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            String m_pref = dataSnapshot.child(inputID).child("preferences").getValue().toString();

                            User user = new User(inputID, inputPW, m_pref);

                            // 성공하면 어디로 가야함
                            /// 여기다가 하세여
                            Intent intent = new Intent(getApplicationContext(), ChooseMapActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                        else {
                            // 비밀번호 틀렸어
                            YoYo.with(Techniques.Shake)
                                    .duration(1000)
                                    .playOn(loginButton);
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            editID.setText("");
                            editPW.setText("");
                            return;
                        }
                    }
                }
                // Toast.makeText(LoginActivity.this, "로그인 정보를 확인하세요.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
