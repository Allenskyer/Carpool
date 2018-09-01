package com.tj.carpool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.tj.carpool.common.HttpHelper;
import com.tj.carpool.datastructure.Passenger;

import org.json.JSONObject;

public class PassengerRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_register);

        Button regBtn = (Button) findViewById(R.id.reg_btn_register);

        final EditText usernameEditText = (EditText) findViewById(R.id.reg_edit_username);
        final EditText passwordEditText = (EditText) findViewById(R.id.reg_edit_pwd);
        final EditText repasswordEditText = (EditText) findViewById(R.id.reg_edit_repwd);
        final EditText realnameEditText = (EditText) findViewById(R.id.reg_edit_realname);
        final EditText phoneEditText = (EditText) findViewById(R.id.reg_edit_phone);
        final RadioButton maleRadioButton = (RadioButton) findViewById(R.id.radio_male);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Passenger passenger = new Passenger();
                passenger.setUserName(usernameEditText.getText().toString().replace(" ",""));
                passenger.setPassword(passwordEditText.getText().toString().replace(" ",""));
                String repassword = repasswordEditText.getText().toString().replace(" ","");
                passenger.setPhoneNum(phoneEditText.getText().toString().replace(" ",""));
                passenger.setName(realnameEditText.getText().toString());
                passenger.setGender(maleRadioButton.isChecked()?"m":"f");

                if(passenger.getUsername().length()<4||passenger.getUsername().length()>20)
                {
                    Toast.makeText(PassengerRegister.this,"用戶名长度需在4-20字符",Toast.LENGTH_SHORT).show();
                }
                else if(passenger.getName().length()<1||passenger.getName().length()>20)
                {
                    Toast.makeText(PassengerRegister.this,"真实姓名长度需在1-20字符",Toast.LENGTH_SHORT).show();
                }
                else if(passenger.getPhoneNum().length()!=11)
                {
                    Toast.makeText(PassengerRegister.this,"请输入11位的手机号",Toast.LENGTH_SHORT).show();
                }
                else if(passenger.getPassword().length()<6||passenger.getPassword().length()>20)
                {
                    Toast.makeText(PassengerRegister.this,"密码长度需在6-20字符",Toast.LENGTH_SHORT).show();
                }
                else if(!repassword.equals(passenger.getPassword()))
                {
                    Toast.makeText(PassengerRegister.this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try{
                        JSONObject sendJsonObject = new JSONObject();
                        sendJsonObject.put("info",passenger);

                        HttpHelper httpHelper = new HttpHelper();
                        String address = getResources().getString(R.string.server_addr)+getResources().getString(R.string.passenger_register);
                        httpHelper.ServletPost(address,sendJsonObject);

                        JSONObject returnJsonObject = new JSONObject(httpHelper.getResult());
                        if(returnJsonObject.has("state"))
                        {
                            int state = returnJsonObject.getInt("state");

                            switch(state)
                            {
                                case 1://注册成功，跳转到登录界面
                                    Intent intent = new Intent(PassengerRegister.this,LoginActivity.class);
                                    intent.putExtra("userType","passenger");
                                    Toast.makeText(PassengerRegister.this,getResources().getString(R.string.register_success),Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                    break;
                                case 2://手机号已被注册
                                    Toast.makeText(PassengerRegister.this,getResources().getString(R.string.exist_phone),Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(PassengerRegister.this,getResources().getString(R.string.unknown_error),Toast.LENGTH_SHORT).show();
                                    break;

                            }
                        }
                        else
                        {
                            Toast.makeText(PassengerRegister.this,getResources().getString(R.string.server_connect_fail),Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }

            }
        });
    }
}
