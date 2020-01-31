package com.project.e_fine.Citizens.Authentications;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.e_fine.R;
import com.project.e_fine.TextViewDatePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    private EditText mUsernameInput, mIDInput, mPasswordInput,mEmailInput, mdate, mConfPasswordInput, mPhoneInput;
    private Button mCreateBtn;
    ImageView mimage ;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;

    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private  StorageReference refstorage;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup_citizen);

        mUsernameInput = findViewById(R.id.input_username);
        mIDInput = findViewById(R.id.input_sign_up_id);
        mPasswordInput = findViewById(R.id.input_sign_up_password);
        mPhoneInput = findViewById(R.id.input_phone_number);
        mEmailInput = findViewById(R.id.input_sign_up_email_address);

        mdate = findViewById(R.id.indate);
        mimage = findViewById(R.id.imgView);

        TextViewDatePicker editTextDatePicker = new TextViewDatePicker(this, mdate);


        mConfPasswordInput = findViewById(R.id.input_confirm_password);
        mCreateBtn = findViewById(R.id.btn_sign_up);
        TextView back_to_login = findViewById(R.id.login);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Signing up...");
        mProgress.setCancelable(false);


        mimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


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
                String id = mIDInput.getText().toString().trim();
                String password = mPasswordInput.getText().toString().trim();
                String confPass = mConfPasswordInput.getText().toString().trim();
                String phone = mPhoneInput.getText().toString().trim();
                String date = mdate.getText().toString().trim();
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
                if (TextUtils.isEmpty(date)) {
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
                        Toast.makeText(SignupActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                    } else {
                        createUser(username, email, id, date, phone, password);
                    }
                }

            }
        });
    }


    private void createUser(final String username, String email, final String id, final String date, final String phone, String password) {

        mProgress.show();
        uploadImage();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username).build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Map userMap = new HashMap();
                                userMap.put("phoneNumber", phone);
                                userMap.put("ID", id );
                                userMap.put("Fullname", username);
                                userMap.put("Date of Birth", date);

                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(mAuth.getCurrentUser().getUid()).setValue(userMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    sendVerificationEmail();

//                                                    Intent mainIntent = new Intent(SignupActivity.this, Dashboard.class);
//                                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                    startActivity(mainIntent);
//                                                    finish();

                                                } else {
                                                    mProgress.dismiss();
                                                    Toast.makeText(SignupActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else {
                                mProgress.dismiss();
                                Toast.makeText(SignupActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    String s = "Sign up Failed" + task.getException();
                    Toast.makeText(SignupActivity.this, s,
                            Toast.LENGTH_SHORT).show();


                    mProgress.dismiss();
//                    Toast.makeText(SignupActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    void uploadImage(){

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Saving...Please Wait");
            progressDialog.show();

            StorageReference ref = storageReference.child("CoverPage/"+ UUID.randomUUID().toString());

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //and you can convert it to string like this:
                    final String sdownload_url = downloadUrl.toString();


//                Log.d(TAG, "onSuccess:" + sdownload_url);
//                Toast.makeText(PoliceDashBoard.this, "" + sdownload_url, Toast.LENGTH_SHORT).show();
//
//
                    Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                            .limitToLast(1);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childdata : dataSnapshot.getChildren()) {
                                childdata.getRef().child("URL").setValue(sdownload_url);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            throw databaseError.toException(); // never ignore errors
                        }
                    });
                    return;
                }
            });

        }

    }

    void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        mimage.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mimage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent


                            // after email is sent just logout the user and finish this activity
                            mProgress.dismiss();
//                                                    Intent mainIntent = new Intent(SignupActivity.this, MainActivity.class);
//                                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                    startActivity(mainIntent);
//                                                    finish();

                            AlertDialog alertDialog = new AlertDialog.Builder(SignupActivity.this).create();
                            alertDialog.setTitle("Verification");
                            alertDialog.setMessage("Check your email to verify your account");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    (dialog, which) -> {
                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        dialog.dismiss();
                                        finish();
                                    });
                            alertDialog.show();

                        } else {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            Toast.makeText(SignupActivity.this, "Email not sent", Toast.LENGTH_SHORT).show();
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }
}
