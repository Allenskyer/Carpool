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

        //final RadioButton passengerBtn = (RadioButton) findViewById(R.id.btn_radio_passenger);
        final RadioButton driverBtn = (RadioButton) findViewById(R.id.btn_radio_driver);

        Button nextStepBtn = (Button) findViewById(R.id.btn_next);

        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userType = "passenger";
                if(driverBtn.isChecked())
                {
                    userType = "driver";
                }
//                Bundle bundle = new Bundle();
//                bundle.putString("userType",userType);

                Intent intent = new Intent(ChooseUserTypeActivity.this,LoginActivity.class);
                intent.putExtra("userType",userType);

                startActivity(intent);
            }
        });
    }
}
