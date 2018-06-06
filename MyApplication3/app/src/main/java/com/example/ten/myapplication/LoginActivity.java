package com.example.ten.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    EditText editID;
    EditText editPW;
    Button loginButton;

    String inputID;
    String inputPW;

    private CallbackManager callbackManager;

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
                            Toast.makeText(LoginActivity.this, "페이스북 로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this, ShowMapActivity.class);
                            startActivity(i);
                            finish();
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
//<<<<<<< HEAD
                            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
//=======
                            Intent i = new Intent(LoginActivity.this, ShowMapActivity.class);
                            i.putExtra("curUser", inputID);
                            startActivity(i);
                            finish();

//>>>>>>> 2f6d8d3d73a25a11dd1792d711d7fa5d2a64f83c
                        } else {
                            // 비밀번호 틀렸어
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
