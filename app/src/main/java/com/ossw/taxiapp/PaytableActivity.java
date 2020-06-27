package com.ossw.taxiapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import com.ossw.taxiapp.CustomerMenuActivity;
import com.ossw.taxiapp.MainActivity;
import com.ossw.taxiapp.R;

public class PaytableActivity extends AppCompatActivity {
    private Button back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytable);

        back = (Button)findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaytableActivity.this, CustomerMenuActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }


}
