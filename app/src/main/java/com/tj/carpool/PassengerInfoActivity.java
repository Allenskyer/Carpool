package com.tj.carpool;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tj.carpool.common.HttpHelper;
import com.tj.carpool.datastructure.Passenger;

public class PassengerInfoActivity extends AppCompatActivity {
    private Passenger passenger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_info);
        passenger = (Passenger) getIntent().getSerializableExtra("userinfo");

        TextView idTextView = (TextView) findViewById(R.id.info_txt_id);
        final TextView usernameTextView = (TextView) findViewById(R.id.info_txt_username);
        TextView realnameTextView = (TextView) findViewById(R.id.info_txt_realname);
        TextView phoneTextView = (TextView) findViewById(R.id.info_txt_phone);
        TextView sexTextView = (TextView) findViewById(R.id.info_txt_sex);
        TextView dateTextView = (TextView) findViewById(R.id.info_txt_regtime);

        idTextView.setText(""+passenger.getID());
        usernameTextView.setText(passenger.getUsername());
        realnameTextView.setText(passenger.getName());
        phoneTextView.setText(passenger.getPhoneNum());
        sexTextView.setText(passenger.getGender().equals("m")?getResources().getString(R.string.male):getResources().getString(R.string.female));
        //dateTextView.setText(passenger.getRegtime());

        ImageButton usernameImageButton = (ImageButton) findViewById(R.id.info_btn_username);
        ImageButton realnameImageButton = (ImageButton) findViewById(R.id.info_btn_realname);
        ImageButton phoneImageButton = (ImageButton) findViewById(R.id.info_btn_phone);
        ImageButton sexImageButton = (ImageButton) findViewById(R.id.info_btn_sex);

        usernameImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText=new EditText(PassengerInfoActivity.this);
                String text = usernameTextView.getText().toString();
                editText.setText(text);
                editText.setSelection(text.length());
                new AlertDialog.Builder(PassengerInfoActivity.this)
                        .setTitle("请输入" )
                        .setView(editText)
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface arg0, int arg1){
                                passenger.setUserName(editText.getText().toString());

                                HttpHelper httpHelper = new HttpHelper();
                                String address = getResources().getString(R.string.server_addr)+"PassengerUpdateServlet";
                            }})
                        .show();
            }
        });

    }
}
