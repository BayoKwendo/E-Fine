package com.project.e_fine.Admin;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.project.e_fine.MainActivity;
import com.project.e_fine.R;

public class AdminLogin extends AppCompatActivity {

    private EditText mEmailInput, mPasswordInput;
    private TextView mSignUpTv;
    private Button mLoginBtn;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;

    private static final String TAG = AdminLogin.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.admin_login);
        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        mAuth = FirebaseAuth.getInstance();


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Authenticating...");
        mProgress.setCancelable(false);

        mEmailInput = findViewById(R.id.input_login_email);
        mPasswordInput = findViewById(R.id.input_login_password);
        mSignUpTv = findViewById(R.id.tv_sign_up);
        mLoginBtn = findViewById(R.id.btn_login);
        Button back = findViewById(R.id.btn_back);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmailInput.getText().toString().trim();
                String password = mPasswordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmailInput.setError("Email Required");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPasswordInput.setError("Password Required");
                    return;
                }

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    login(email, password);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void login(String email, String password) {

        mProgress.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mProgress.dismiss();
                    admin();

                } else {
                    mProgress.dismiss();
                    Toast.makeText(AdminLogin.this, "Failed to sign in. Please try again.", Toast.LENGTH_LONG).show();

                }
            }
        });

    }
    void admin(){
        mAuth = FirebaseAuth.getInstance();
        String currentUid = mAuth.getCurrentUser().getUid();
            if (currentUid.equals("WOXauKemfQRVaTLGNUGgw5lkxta2")){
//                Toast.makeText(this, "Welcome Sir/Madam", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(AdminLogin.this,  QuestionsActivity.class));
//                finish();
                startActivity(new Intent(AdminLogin.this,  Add_Police.class));

            }
            else {
                Toast.makeText(AdminLogin.this, "Your Not an Admin. Try Again!!", Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(AdminLogin.this,  MainActivity.class));
        finish();


    }
}
