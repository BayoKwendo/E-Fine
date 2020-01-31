package com.project.e_fine.Police;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.e_fine.Admin.AdminLogin;
import com.project.e_fine.MainActivity;
import com.project.e_fine.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Police_LoginActivity extends AppCompatActivity {

    private static final String TAG = Police_LoginActivity.class.getSimpleName();
    DatabaseReference databaseReference;
    private EditText mPasswordInput, mIDInput, mPasswordInput1;
    private TextView mSignUpTv, mReset, Admin;
    private Button mLoginBtn, mLoginBtn1;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LinearLayoutCompat layout1, layout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_police);
        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

//        mAuth = FirebaseAuth.getInstance();
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if (mAuth.getCurrentUser() != null) {
////                    startActivity(new Intent(Police_LoginActivity.this, Dashboard.class));
////                    finish();
//                }
//            }
//        };


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Authenticating...");
        mProgress.setCancelable(true);
        mIDInput = findViewById(R.id.input_login_id);
        mPasswordInput = findViewById(R.id.input_login_password);
        mSignUpTv = findViewById(R.id.tv_sign_up);
        mReset = findViewById(R.id.tv_forgot_password);
        Admin = findViewById(R.id.admin);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Police");


        mLoginBtn = findViewById(R.id.btn_login);


        layout1 = findViewById(R.id.layout1);


        mLoginBtn = findViewById(R.id.btn_login);

        mLoginBtn = findViewById(R.id.btn_login);
//        mSignUpTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Police_LoginActivity.this, SignupActivity.class));
//            }
//        });
//
//
//        mReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Police_LoginActivity.this, ResetPasswordActivity.class));
//            }
//        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pid = mIDInput.getText().toString().trim();
                String password = mPasswordInput.getText().toString().trim();


                if (TextUtils.isEmpty(pid)) {
                    mIDInput.setError("ID Required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPasswordInput.setError("Password Required");
                    return;
                }

                if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(password)) {

                    login(pid, password);
                }
            }
        });

//        Admin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(Police_LoginActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
////                startActivity(new Intent(Police_LoginActivity.this, com.navigatpeer.deaf.Police_LoginActivity.class));
//            }
//        });

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void login(String pid, String password) {


        mProgress.show();

        Query dbRefFirstTimeCheck = databaseReference.orderByChild("Police ID").equalTo(pid);
//        Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mProgress.dismiss();
                    dialogue_error1();


                } else {

                    Query dbRefSecondTimeCheck = databaseReference.orderByChild("Password").equalTo(password);
                    //Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
                    dbRefSecondTimeCheck.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mProgress.dismiss();
                                startActivity(new Intent(Police_LoginActivity.this,  PoliceDashBoard.class));

                            }
                            else {
                                dialogue_error2();


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            throw databaseError.toException(); // don't ignore errors
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Police_LoginActivity.this,  MainActivity.class));

        finish();

    }

    void dialogue_error1() {

        new SweetAlertDialog(Police_LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Police ID not foubd")
                .setContentText("Police id entered not found. Please try again!")
                .setConfirmText("ok")
                .show();

    }
    void dialogue_error2() {

        new SweetAlertDialog(Police_LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Password incorrect")
                .setContentText("Password entered is incorrect")
                .setConfirmText("ok")
                .show();

    }

}
