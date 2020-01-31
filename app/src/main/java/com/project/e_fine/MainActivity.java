package com.project.e_fine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.e_fine.Admin.AdminLogin;
import com.project.e_fine.Citizens.Authentications.LoginActivity;
import com.project.e_fine.Police.PoliceDashBoard;
import com.project.e_fine.Police.Police_LoginActivity;

public class MainActivity extends AppCompatActivity {

    Button btn1, btn2, btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        btn1 = findViewById(R.id.admin);
        btn2 = findViewById(R.id.police);
        btn3 = findViewById(R.id.citizen);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,  AdminLogin.class));

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,  Police_LoginActivity.class));

            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,  LoginActivity.class));

            }
        });
    }

}
