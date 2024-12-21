package com.example.electrohub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.QuickContactBadge;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SigninActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        EditText userid,pass;
        AppCompatButton btnsignin;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnsignin = findViewById(R.id.signipButton);
        userid = findViewById(R.id.signipEmailEdt);
        pass = findViewById(R.id.signipPasswordEdt);

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userid.getText().toString().isEmpty() || !userid.getText().toString().equals("Admin")){
                    userid.setError("Please Enter Admin");
                } else if (pass.getText().toString().isEmpty() || !pass.getText().toString().equals("admin")) {
                    pass.setError("Please Enter admin");
                }
                else if(userid.getText().toString().equals("Admin") && pass.getText().toString().equals("admin")){
                    Intent i = new Intent(SigninActivity.this,MainActivity.class);
                    startActivity(i);
                }
            }
        });

    }
}