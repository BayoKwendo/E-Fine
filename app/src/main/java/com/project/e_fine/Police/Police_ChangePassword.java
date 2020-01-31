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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.e_fine.Citizens.Authentications.SignupActivity;
import com.project.e_fine.MainActivity;
import com.project.e_fine.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Police_ChangePassword extends AppCompatActivity {

    private static final String TAG = Police_ChangePassword.class.getSimpleName();
    DatabaseReference databaseReference;
    private EditText mPasswordInput, mIDInput, mConfPasswordInput;
    private TextView mSignUpTv, mReset, btnBack, Admin;
    private Button mLoginBtn, mLoginBtn1;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ConstraintLayout layout1, layout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_password);
        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Authenticating...");
        mIDInput = findViewById(R.id.input_sign_up_police_id);
        btnBack = (Button) findViewById(R.id.btn_back);

        mPasswordInput = findViewById(R.id.input_sign_up_password);
        mConfPasswordInput = findViewById(R.id.input_confirm_password);

        mReset = findViewById(R.id.btn_change);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Police");



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pid = mIDInput.getText().toString().trim();
                String password = mPasswordInput.getText().toString().trim();
                String confPass = mConfPasswordInput.getText().toString().trim();


                if (TextUtils.isEmpty(pid)) {
                    mIDInput.setError("ID Required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPasswordInput.setError("Password Required");
                    return;
                }

                if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confPass)) {
                    if (!confPass.equals(password)) {
                        Toast.makeText(Police_ChangePassword.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                    } else {
                        login(pid, password);
                    }
                }

                if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(password)) {


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
                    dialogue_success();
                    Query dbRefSecondTimeCheck = databaseReference.orderByChild("Police ID").equalTo(pid);
                    //Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
                    dbRefSecondTimeCheck.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childdata : dataSnapshot.getChildren()) {
                                 childdata.getRef().child("Password").setValue(password);
                            }

                            }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

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
        finish();

    }

    void dialogue_error1() {

        new SweetAlertDialog(Police_ChangePassword.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Password change Unsuccessful! ")
                .setContentText("Police id entered not found. Please try again!")
                .setConfirmText("ok")
                .show();

    }
    void dialogue_success() {

        new SweetAlertDialog(Police_ChangePassword.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Password change Successful! ")
                .setContentText("Your Password was changed successful")
                .setConfirmText("ok")
                .show();

        mProgress.dismiss();

    }

}
