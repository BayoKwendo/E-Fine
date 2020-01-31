package com.project.e_fine.Citizens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.e_fine.Admin.AdminLogin;
import com.project.e_fine.MainActivity;
import com.project.e_fine.R;
import com.project.e_fine.TextViewDatePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User_Profile extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 71;
    StorageReference storageReference;
    private EditText mNameField, mPhoneField, mIDField, mNumberField;
    private Button mFine, mConfirm;
    private ImageView mProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference mCourierDatabase;
    private String userID;
    private String mName;
    private Uri filePath;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mCustomerDatabase;
    private String mPhone;
    private String mNumber;
    private String mID;
    private String mProfileImageUrl;

    private Uri resultUri;


    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        TextView tool = toolbar.findViewById(R.id.title);
//        tool.setText("Profile");
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setElevation(0);
//        }
//

        mNameField = findViewById(R.id.name);
        mIDField = findViewById(R.id.id);
        mPhoneField = findViewById(R.id.phone);
        mNumberField = findViewById(R.id.number);
        TextViewDatePicker editTextDatePicker = new TextViewDatePicker(this, mNumberField);
        mProfileImage = findViewById(R.id.profileImage);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mFine = findViewById(R.id.fine);
        mConfirm = findViewById(R.id.confirm);
        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCourierDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                saveUserInformation();
            }
        });

        mFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                next();

//                finish();
//                return;
            }
        });
    }

    private void getUserInfo() {
        mCourierDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Fullname") != null) {
                        mName = map.get("Fullname").toString();
                        mNameField.setText(mName);
                    }
                    if (map.get("phoneNumber") != null) {
                        mPhone = map.get("phoneNumber").toString();
                        mPhoneField.setText(mPhone);
                    }
                    if (map.get("Date of Birth") != null) {
                        mNumber = map.get("Date of Birth").toString();

                        mNumberField.setText(mNumber);
                    }
                    if (map.get("ID") != null) {
                        mID = map.get("ID").toString();

                        mIDField.setText(mID);
                    }

                    if (map.get("URL") != null) {
                        mProfileImageUrl = map.get("URL").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);


                        Toast.makeText(User_Profile.this, "" + mProfileImageUrl, Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }


    void next()
    {

        Intent topicsIntent = new Intent(User_Profile.this, Portal.class);
        topicsIntent.putExtra("userID", mID);
        startActivity(topicsIntent);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

//    private void saveUserInformation() {
//        mProgressDialog.setMessage("Saving... \n Please Wait.. :)");
//        mProgressDialog.show();
//
//        mName = mNameField.getText().toString();
//        mPhone = mPhoneField.getText().toString();
//        mNumber = mNumberField.getText().toString();
//
//
//        Map<String, Object> userInfo = new HashMap<>();
//        userInfo.put("Fullname", mName);
//        userInfo.put("ID", mID);
//        userInfo.put("phoneNumber", mPhone);
//        userInfo.put("Date of Birth", mNumber);
//        mCourierDatabase.updateChildren(userInfo);
//
//
//        if (filePath != null) {
//
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Saving...Please Wait");
//            progressDialog.show();
//
//            StorageReference ref = storageReference.child("CoverPage/" + UUID.randomUUID().toString());
//
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), filePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
//            byte[] data = baos.toByteArray();
//            UploadTask uploadTask = ref.putBytes(data);
//
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    progressDialog.dismiss();
//                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    //and you can convert it to string like this:
//                    final String sdownload_url = downloadUrl.toString();
//
//
////                Log.d(TAG, "onSuccess:" + sdownload_url);
////                Toast.makeText(PoliceDashBoard.this, "" + sdownload_url, Toast.LENGTH_SHORT).show();
//
//
//                    Query query = FirebaseDatabase.getInstance().getReference().child("Users")
//                            .limitToLast(1);
//                    query.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for (DataSnapshot childdata : dataSnapshot.getChildren()) {
//                                childdata.getRef().child("URL").setValue(sdownload_url);
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            throw databaseError.toException(); // never ignore errors
//                        }
//                    });
//                    return;
//                }
//            });
//
//        } else {
//
//            Toast.makeText(this, "Kindly Upload Image again", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {

                e.printStackTrace();

            }
        }
    }
}
