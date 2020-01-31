package com.project.e_fine.Police;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.e_fine.MainActivity;
import com.project.e_fine.R;
import com.project.e_fine.TextViewDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PoliceDashBoard extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final int PICK_IMAGE_REQUEST = 71;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    String[] fineNames = {
            "Driver of a motorcycle carrying more than one passenger Ksh.1000",
            "Causing obstruction by allowing vehicle to remain in any position on the road inconveniencing other motorists Ksh.10,000", "Failure to stop when required to do so by a police officer in uniform Ksh.5000", "Failure by driver to obey instructions given by a police officer in uniform verbally or by signal Ksh.3000",
            "Driving through a pavement or pedestrian walkway Ksh.5000",
            "Exceeding speed limit Ksh.500-10,000 (depending on the Kph warning)",
            "Failure to carry and produce drivers license on demand Ksh.1000",
            "Driving a PSV while being unqualified Ksh.5000",
            "Failure to renew drivers license Ksh.1000",
            "Driving without a valid drivers endorsement in respect of the class of the vehicle Ksh.3000",
            "Driving a vehicle without valid inspection certificate Ksh.10,000",
            "Failure to carry fire extinguisher and fire kits Ksh.2000",
            "Driver picking up passenger at an unauthorised bus stop Ksh.3000",
            "Passenger alighting at an unauthorised bus stop Ksh.1000",
            "Driver using a mobile phone while vehicle is in motion Ksh.2000",
            "Driving without identification plates affixed or plates not fixed in the prescribed manner Ksh.10,000"
    };
    Button btn, choosebtn;
    private EditText mdates, mid, shortiput, longinpit, mprice;
    private DatabaseReference Fines;
    private ImageView image;
    private String TAG;
    private Boolean isLoggingOut = false;
    private Uri filePath;
    private StorageReference refstorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_report);

        final Spinner spin = findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);
        mdates = findViewById(R.id.mdate);
        TextViewDatePicker editTextDatePicker = new TextViewDatePicker(this, mdates);
        mid = findViewById(R.id.user_id);
        longinpit = findViewById(R.id.shipper_field);
        mprice = findViewById(R.id.price);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Fines = FirebaseDatabase.getInstance().getReference().child("Fines");

        btn = findViewById(R.id.post);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter<String> aa = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                fineNames);
        aa.setDropDownViewResource(R.layout.spinner_text);
//Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(PoliceDashBoard.this);
                progressDialog.setTitle("Posting...Please Wait");


                int crimeprice = 0;
                try {
                    crimeprice = Integer.parseInt(mprice.getText().toString());
                } catch (NumberFormatException e) {
                    // Log error, change value of temperature, or do nothing
                }

                int muser_id = 0;
                try {
                    muser_id = Integer.parseInt(mid.getText().toString());
                } catch (NumberFormatException e) {
                    // Log error, change value of lowerBlood, or do nothing
                }

                final String mlonginput = longinpit.getText().toString();

//
//                if (TextUtils.isEmpty(crimeprice)) {
//                    mprice.setError("This field is required");
//                    return;
//                }
//                if (TextUtils.isEmpty(date)) {
//                    mid.setError("This field is required");
//                    return;
//                }


                if (TextUtils.isEmpty(mlonginput)) {
                    Toast.makeText(PoliceDashBoard.this, "Crime description required", Toast.LENGTH_SHORT).show();
                    longinpit.setError("This field is required");
                    return;
                } else {
                    progressDialog.show();
//                if (TextUtils.isEmpty(muser_id)) {
//                    mid.setError("This field is required");
//                    return;
//                }
//                if (muser_id.length() <= 10) {
//                    mid.setError("Username atleast four character ");
//                    return;
//                }


                    String Text = spin.getSelectedItem().toString();

                    Map<String, java.io.Serializable> topicMap = new HashMap<String, java.io.Serializable>();
                    topicMap.put("price", crimeprice);
                    topicMap.put("LongDescription", mlonginput);
                    topicMap.put("Date of Offence", getTime());
                    topicMap.put("User ID", muser_id);
                    topicMap.put("Type of the Offence", Text);


                    Fines.push().setValue(topicMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                dialogue();
                            } else {
                                Toast.makeText(PoliceDashBoard.this, "EEOR", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                }
            }
        });


    }

    void dialogue() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("SUCCESS")
                .setContentText("Data Saved Successful!!")
                .setConfirmText("ok")
                .show();
        mdates.setText("");
        mid.setText("");
        longinpit.setText("");
        mprice.setText("");

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        // Return true to display menu
//        return true;
//    }


    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());

        return currentDateandTime;
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

//        Toast.makeText(getApplicationContext(), fineNames[position], Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

        Toast.makeText(this, "Select at least one crime", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leaving?")
                .setMessage("Are you sure you want to leave this page?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        PoliceDashBoard.super.onBackPressed();

                        startActivity(new Intent(PoliceDashBoard.this, MainActivity.class));
                    }
                }).create().show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        // Lofcate MenuItem with ShareActionProvider
//        MenuItem item = menu.findItem(R.id.logout);
//
//
//        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as Forums specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change) {

            startActivity(new Intent(PoliceDashBoard.this, Police_ChangePassword.class));

        }
        return super.onOptionsItemSelected(item);
    }


}

