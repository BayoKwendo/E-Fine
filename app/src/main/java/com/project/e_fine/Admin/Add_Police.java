package com.project.e_fine.Admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.e_fine.MainActivity;
import com.project.e_fine.Police.PoliceDashBoard;
import com.project.e_fine.R;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Add_Police extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;
    private EditText mUsernameInput, mPoliceID, mIDInput, mPasswordInput, mEmailInput, mConfPasswordInput, mPhoneInput;
    private Button mCreateBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    DatabaseReference databaseReference;
    private Uri filePath;
    private StorageReference refstorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_polic);

        mUsernameInput = findViewById(R.id.input_username);
        mIDInput = findViewById(R.id.input_sign_up_id);
        mPasswordInput = findViewById(R.id.input_sign_up_password);
        mPhoneInput = findViewById(R.id.input_phone_number);
        mEmailInput = findViewById(R.id.input_sign_up_email_address);
        mPoliceID = findViewById(R.id.input_sign_up_police_id);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Police");



        mConfPasswordInput = findViewById(R.id.input_confirm_password);
        mCreateBtn = findViewById(R.id.btn_sign_up);
        TextView back_to_login = findViewById(R.id.login);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Data saving...");
        mProgress.setCancelable(false);


        mAuth = FirebaseAuth.getInstance();
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsernameInput.getText().toString().trim();
                String email = mEmailInput.getText().toString().trim();
                String pid = mPoliceID.getText().toString().trim();
                String id = mIDInput.getText().toString().trim();
                String password = mPasswordInput.getText().toString().trim();
                String confPass = mConfPasswordInput.getText().toString().trim();
                String phone = mPhoneInput.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (TextUtils.isEmpty(username)) {
                    mUsernameInput.setError("This field is required");


                    return;
                }
                if (username.length() <= 4) {
                    mUsernameInput.setError("Username atleast four character ");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmailInput.setError("This field is required");
                    return;
                }
                if (TextUtils.isEmpty(id)) {
                    mIDInput.setError("This field is required");
                    return;
                }
                if (TextUtils.isEmpty(pid)) {
                    mIDInput.setError("This field is required");
                    return;
                }
                if (!(email.matches(emailPattern))) {

                    mEmailInput.setError("Email not Valid");

                }

                if (TextUtils.isEmpty(password)) {
                    mPasswordInput.setError("This field is required");
                    return;
                }

                if (TextUtils.isEmpty(confPass)) {
                    mConfPasswordInput.setError("This field is required");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    mPhoneInput.setError("This field is required");
                    return;
                }

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(id) && !TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(confPass)) {
                    if (!confPass.equals(password)) {
                        Toast.makeText(Add_Police.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                    } else {
                        createUser(username, email, id, pid, phone, password);
                    }
                }

            }
        });
    }


    private void createUser(final String username, String email, final String id, final String pid, final String phone, String password) {

        mProgress.show();


        Query dbRefFirstTimeCheck = databaseReference.orderByChild("Police ID").equalTo(pid);
//        Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mProgress.dismiss();
                    dialogue_error();

                }else {

                    Map userMap = new HashMap();
                    userMap.put("phoneNumber", phone);
                    userMap.put("Nationa ID", id);
                    userMap.put("Police ID", pid);
                    userMap.put("Email", email);
                    userMap.put("Fullname", username);
                    userMap.put("Password", password);


                    FirebaseDatabase.getInstance().getReference().child("Police")
                            .child(mAuth.getCurrentUser().getUid()).setValue(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgress.dismiss();
                                        dialogue();

                                    } else {
                                        mProgress.dismiss();
                                        Toast.makeText(Add_Police.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                                    }
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

    void dialogue() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("SUCCESS")
                .setContentText("Data Saved Successful!!")
                .setConfirmText("ok")
                .show();

        mUsernameInput.setText("");
        mEmailInput.setText("");
        mIDInput.setText("");
        mPoliceID.setText("");
        mPasswordInput.setText("");

    }
    void dialogue_error() {

        new SweetAlertDialog(Add_Police.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Police Exist")
                .setContentText("Police add is already existing in our system")
                .setConfirmText("ok")
                .show();


        mUsernameInput.setText("");
        mEmailInput.setText("");
        mIDInput.setText("");
        mPoliceID.setText("");
        mPasswordInput.setText("");

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leaving?")
                .setMessage("Are you sure you want to leave this page?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Add_Police.super.onBackPressed();

                        startActivity(new Intent(Add_Police.this, MainActivity.class));
                    }
                }).create().show();

    }


}
