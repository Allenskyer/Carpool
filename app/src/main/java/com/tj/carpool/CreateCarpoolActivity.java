package com.tj.carpool;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tj.carpool.common.HttpHelper;
import com.tj.carpool.datastructure.CarpoolActivity;
import com.tj.carpool.datastructure.Location;
import com.tj.carpool.datastructure.Passenger;

import org.json.JSONObject;

import java.sql.Timestamp;

public class CreateCarpoolActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_carpool);

        TextView oriTextView = (TextView) findViewById(R.id.txt_origin);
        TextView destTextView = (TextView) findViewById(R.id.txt_dest);

        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.num_picker);
        numberPicker.setMaxValue(4);
        numberPicker.setMinValue(1);
        final DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
        //TODO:设置日期范围
        //datePicker.setMaxDate();
        //datePicker.set

        final TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);


        final Location oriLocation = new Location();
        final Location destLocation = new Location();

        oriLocation.setLatitude(getIntent().getDoubleExtra("oriLatitude",0));
        destLocation.setLongitude(getIntent().getDoubleExtra("oriLongitude",0));

        oriTextView.setText(getIntent().getStringExtra("oriAddress"));
        destTextView.setText(getIntent().getStringExtra("destAddress"));

        Button confirButton = (Button) findViewById(R.id.btn_confirm);

        confirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try{
                            Long dTime = new Timestamp(datePicker.getYear(),
                                    datePicker.getMonth(),datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(),timePicker.getCurrentMinute(),0,0).getTime();
                            //TODO：添加beartime前端
                            Long bearTime = new Timestamp(0,0,0,0,30,0,0).getTime();

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("dLocation",oriLocation);
                            jsonObject.put("tLocation",destLocation);
                            jsonObject.put("dTime",dTime);
                            jsonObject.put("targetNum",3);
                            jsonObject.put("userId",getIntent().getIntExtra("ID",0));
                            jsonObject.put("passengerNum",numberPicker.getValue());
                            jsonObject.put("bearTime",bearTime);

                            HttpHelper httpHelper = new HttpHelper();
                            if(httpHelper.ServletPost(getResources().getString(R.string.server_addr)+getResources().getString(R.string.create_activity),jsonObject))
                            {
                                JSONObject returnJsonObject = new JSONObject(httpHelper.getResult());
                                if(!returnJsonObject.has("state"))
                                {
                                    Toast.makeText(CreateCarpoolActivity.this,getResources().getString(R.string.create_fail),Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    int state = returnJsonObject.getInt("state");

                                    if(state == 1)//创建成功
                                    {
                                        Toast.makeText(CreateCarpoolActivity.this,getResources().getString(R.string.create_success),Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(CreateCarpoolActivity.this,MainActivity.class);

                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(CreateCarpoolActivity.this,getResources().getString(R.string.unknown_error),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            else
                            {
                                Toast.makeText(CreateCarpoolActivity.this,getResources().getString(R.string.server_connect_fail),Toast.LENGTH_SHORT).show();

                            }


                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        Looper.loop();
                    }
                }).start();

            }
        });

    }
}
