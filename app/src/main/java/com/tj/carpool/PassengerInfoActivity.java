package com.tj.carpool;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tj.carpool.common.HttpHelper;
import com.tj.carpool.datastructure.Passenger;

import org.json.JSONObject;

public class PassengerInfoActivity extends AppCompatActivity {
    private Passenger passenger;
    boolean changed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_info);
        passenger = (Passenger) getIntent().getSerializableExtra("userinfo");

        TextView idTextView = (TextView) findViewById(R.id.info_txt_id);
        final TextView usernameTextView = (TextView) findViewById(R.id.info_txt_username);
        final TextView realnameTextView = (TextView) findViewById(R.id.info_txt_realname);
        final TextView phoneTextView = (TextView) findViewById(R.id.info_txt_phone);
        final TextView sexTextView = (TextView) findViewById(R.id.info_txt_sex);
        final TextView dateTextView = (TextView) findViewById(R.id.info_txt_regtime);

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
                                changed = true;
                                passenger.setUserName(editText.getText().toString());
                                usernameTextView.setText(editText.getText().toString());
                            }})
                        .show();
            }
        });

        realnameImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText=new EditText(PassengerInfoActivity.this);
                String text = realnameTextView.getText().toString();
                editText.setText(text);
                editText.setSelection(text.length());
                new AlertDialog.Builder(PassengerInfoActivity.this)
                        .setTitle("请输入" )
                        .setView(editText)
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface arg0, int arg1){
                                changed = true;
                                passenger.setName(editText.getText().toString());
                                realnameTextView.setText(editText.getText().toString());
                            }})
                        .show();
            }
        });

        phoneImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText=new EditText(PassengerInfoActivity.this);
                String text = phoneTextView.getText().toString();
                editText.setText(text);
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                editText.setSelection(text.length());
                new AlertDialog.Builder(PassengerInfoActivity.this)
                        .setTitle("请输入" )
                        .setView(editText)
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface arg0, int arg1){
                                changed = true;
                                passenger.setPhoneNum(editText.getText().toString());
                                phoneTextView.setText(editText.getText().toString());
                            }})
                        .show();
            }
        });

        sexImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Button saveButton = (Button) findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(changed)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            try {
                                HttpHelper httpHelper = new HttpHelper();
                                String address = getResources().getString(R.string.server_addr)+"PUpdateAction";

                                Gson gson = new Gson();
                                JSONObject sendJsonObject = new JSONObject(gson.toJson(passenger,Passenger.class));

                                if(httpHelper.ServletPost(address,sendJsonObject))
                                {
                                    JSONObject returnJsonObject = new JSONObject(httpHelper.getResult());
                                    if(!returnJsonObject.has("state"))
                                    {
                                        Toast.makeText(PassengerInfoActivity.this,getResources().getString(R.string.create_fail),Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        int state = returnJsonObject.getInt("state");

                                        if(state == 1)//修改成功
                                        {
                                            Toast.makeText(PassengerInfoActivity.this,getResources().getString(R.string.update_success),Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                        {
                                            Toast.makeText(PassengerInfoActivity.this,getResources().getString(R.string.unknown_error),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                else
                                {
                                    Toast.makeText(PassengerInfoActivity.this,getResources().getString(R.string.server_connect_fail),Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Looper.loop();
                        }
                    }).start();
                }
                else
                {
                    Toast.makeText(PassengerInfoActivity.this,getResources().getString(R.string.nothing_changed),Toast.LENGTH_SHORT).show();

                }


            }
        });

    }
}
