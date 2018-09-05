package com.tj.carpool;

import android.content.Intent;
import android.os.Looper;
import android.service.chooser.ChooserTarget;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;

import com.tj.carpool.common.HttpHelper;
import com.tj.carpool.datastructure.*;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText phoneEditText,passwordEditText;
    Button loginButton,regButton;
    String userType;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this,ChooseUserTypeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userType = this.getIntent().getStringExtra("userType");

        phoneEditText = (EditText) findViewById(R.id.login_edit_phone);
        passwordEditText = (EditText) findViewById(R.id.login_edit_pwd);

        loginButton = (Button) findViewById(R.id.login_btn_login);
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                final String phonenum = phoneEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try{
                            String address = getResources().getString(R.string.server_addr);
                            switch(userType)
                            {
                                case "passenger":
                                    address+=getResources().getString(R.string.passenger_login);
                                    break;
                                default:
                                    address+=getResources().getString(R.string.driver_login);
                            }
                            JSONObject sendJsonObject=new JSONObject() ;
                            sendJsonObject.put("phoneNum", phonenum);
                            sendJsonObject.put("password", password);
                            HttpHelper httpHelper = new HttpHelper();
                            boolean isSuccess = httpHelper.ServletPost(address,sendJsonObject);

                            if(isSuccess)
                            {
                                JSONObject returnJsonObject = new JSONObject(httpHelper.getResult());
                                if(!returnJsonObject.has("state"))
                                {
                                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.login_fail),Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    int state = returnJsonObject.getInt("state");

                                    if(state == 1)//登录成功
                                    {
                                        Toast.makeText(LoginActivity.this,getResources().getString(R.string.login_success),Toast.LENGTH_SHORT).show();
                                        String passengerJson = returnJsonObject.getString("userInfo");
                                        //Passenger passenger = new Passenger();
                                        Gson gson = new Gson();

                                        Passenger passenger = gson.fromJson(passengerJson,Passenger.class);

//                                        passenger.setID(passengerObject.getInt("ID"));
//                                        passenger.setName(passengerObject.getString("Name"));
//                                        passenger.setGender(passengerObject.getString("Gender"));
//                                        passenger.setPhoneNum(passengerObject.getString("PhoneNum"));
//                                        passenger.setUsername(passengerObject.getString("UserName"));
//                                        //passenger.setRegtime(passengerObject.getString("RegTime"));

                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("userInfo",passenger);
                                        bundle.putString("userType",userType);

                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        intent.putExtras(bundle);

                                        startActivity(intent);
                                        finish();
                                    }
                                    else if(state == 3)//手机号未注册
                                    {
                                        Toast.makeText(LoginActivity.this,getResources().getString(R.string.no_phone),Toast.LENGTH_SHORT).show();
                                    }
                                    else if(state == 4)//密码错误
                                    {
                                        Toast.makeText(LoginActivity.this,getResources().getString(R.string.wrong_password),Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this,getResources().getString(R.string.unknown_error),Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this,getResources().getString(R.string.server_connect_fail),Toast.LENGTH_SHORT).show();

                            }
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,"异常",Toast.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                    }
                }).start();




            }
        });

        regButton = (Button) findViewById(R.id.login_btn_register);
        regButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(userType.equals("passenger"))
                {
                    startActivity(new Intent(LoginActivity.this,PassengerRegister.class));
                }
                else
                {
                    startActivity(new Intent(LoginActivity.this,DriverRegister.class));
                }
            }
        });

    }

}
