package com.tj.carpool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class ChooseUserTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user_type);


        Button driverBtn = (Button) findViewById(R.id.btn_driver);
        Button passengerBtn = (Button) findViewById(R.id.btn_passenger);

        driverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChooseUserTypeActivity.this,LoginActivity.class);
                intent.putExtra("userType","driver");

                startActivity(intent);
                finish();
            }
        });

        passengerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChooseUserTypeActivity.this,LoginActivity.class);
                intent.putExtra("userType","passenger");

                startActivity(intent);
                finish();
            }
        });
    }
}
