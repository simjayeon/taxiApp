package com.ossw.taxiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity{

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private EditText mTitle, mContents;
    private Button save, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mTitle = findViewById(R.id.post_title_edit);
        mContents = findViewById(R.id.item_post_contents_edit);

        back = findViewById(R.id.back);
        save = findViewById(R.id.post_save_button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {
                    String postId = mStore.collection(FirebaseID.post).document().getId();
                    Map<String, Object> data = new HashMap<>();
                    data.put(FirebaseID.documentId, mAuth.getCurrentUser().getUid());
                    data.put(FirebaseID.title, mTitle.getText().toString());
                    data.put(FirebaseID.contents, mContents.getText().toString());
                    data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());
                    mStore.collection(FirebaseID.post).document(postId).set(data, SetOptions.merge());
                    Intent intent = new Intent(PostActivity.this, CustomerCommunityActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, CustomerCommunityActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }
}

