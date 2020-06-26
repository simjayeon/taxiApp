package com.ossw.taxiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class DriverMenuActivity extends AppCompatActivity {

    private Button workOn,  d_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_menu);

        workOn = (Button)findViewById(R.id.workOn);



        //기사 로그아웃
        d_logout = (Button)findViewById(R.id.back);
        d_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DriverMenuActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(DriverMenuActivity.this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        });



        workOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DriverMenuActivity.this,"운행을 시작합니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DriverMenuActivity.this, DriverMapActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });



    }
}
