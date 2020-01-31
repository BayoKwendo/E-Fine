package com.project.e_fine.Citizens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.e_fine.Citizens.Authentications.LoginActivity;
import com.project.e_fine.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Portal extends AppCompatActivity {
    View root;
    private FirebaseAuth mAuth;
    private DatabaseReference Fines;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private RecyclerView mForumsRv;
    private LinearLayout mProgressLayout;
    private String userid;
    private TextView mVIEWm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portal_citizen);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        TextView tool = (TextView) toolbar.findViewById(R.id.title);
//        tool.setText("Check Fine");
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setElevation(0);
//        }
        mAuth = FirebaseAuth.getInstance();

        mVIEWm = findViewById(R.id.tv_topics_error);

        Fines = FirebaseDatabase.getInstance().getReference().child("Fines");


        userid = getIntent().getStringExtra("userID");




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(Portal.this, LoginActivity.class));
                    finish();
//                    Toast.makeText(StartDiscussionActivity.this, "You Must Login first", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Context context = Portal.this;
        mForumsRv = findViewById(R.id.rv_forum_list);
        mForumsRv.setLayoutManager(new LinearLayoutManager(context));
        mProgressLayout = findViewById(R.id.layout_progress);



        if (!isNetworkAvailable()) {
            // Create an Alert Dialog
            mProgressLayout.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the Alert Dialog Message
            builder.setMessage("Internet Connection Required");
            builder.setCancelable(false);

            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            builder.setPositiveButton("Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            // Restart the Activity
                            recreate();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        loadData();

    }

    private void loadData() {
        mProgressLayout.setVisibility(View.VISIBLE);

//        Toast.makeText(Portal.this, "" + userid, Toast.LENGTH_SHORT).show();


        int id_number = Integer.valueOf(userid);

        Query query = FirebaseDatabase.getInstance().getReference().child("Fines").orderByChild("User ID").equalTo(id_number);

        FirebaseRecyclerOptions<fineModel> options =
                new FirebaseRecyclerOptions.Builder<fineModel>()
                        .setQuery(query, fineModel.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<fineModel, ForumsViewHolder> adapter = new FirebaseRecyclerAdapter<fineModel, ForumsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ForumsViewHolder holder, final int position, @NonNull fineModel model) {

                // holder.mForumNameTv.setText(model.getForumName());

                holder.mForumFineTv.setText("Pay this Fine!!!\n");

                holder.mDescTv.setText("You have Ksh. " + model.getPrice() + "  Fine to pay! Kindly do so as soon as possible");

                holder.mView.setOnClickListener(new View.OnClickListener() {

                    @Override

                    public void onClick(View v) {
                        Intent topicsIntent = new Intent(Portal.this, GateWay.class);
                        topicsIntent.putExtra("userID", userid);
                        startActivity(topicsIntent);

                    }
                });

                holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        dialogue_error();
                        return true;
                    }
                });

                mProgressLayout.setVisibility(View.GONE);

            }



            @NonNull
            @Override
            public ForumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ForumsViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_fine_row, parent, false));
            }
        };

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {

                   mVIEWm.setVisibility(View.VISIBLE);
                   mForumsRv.setVisibility(View.GONE);

                }
//                else {
//                    Toast.makeText(Portal.this, "NSNNDDDDHHDHDH", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
         });



        adapter.startListening();
        mForumsRv.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {

        finish();


    }

//    @Override
//    public void onStop() {
//        super.onStop();
//         FirebaseAuth.getInstance().signOut();zz
//        loadData();
//
//    }

    void dialogue_error() {

        new SweetAlertDialog(Portal.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Fine!")
                .setContentText("Pay this fine asap!!")
                .setConfirmText("ok")
                .show();


    }

    private class ForumsViewHolder extends RecyclerView.ViewHolder {

        private TextView mForumFineTv, mDescTv;
        private View mView;

        public ForumsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;


//            mForumNameTv = itemView.findViewById(R.id.tv_forum_name);
            mDescTv = itemView.findViewById(R.id.tv_forum_desc);

            mForumFineTv = itemView.findViewById(R.id.tv_fine_name);

        }
    }

}
