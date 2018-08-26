package com.tj.carpool;

import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText,passwordEditText;
    Button loginButton,regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.login_edit_username);
        passwordEditText = (EditText) findViewById(R.id.login_edit_pwd);

        loginButton = (Button) findViewById(R.id.login_btn_login);
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = loginByPost(username,password);
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this,result,Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();

            }
        });


    }

    public String loginByPost(String username,String password){
        String address = getResources().getString(R.string.server_addr)+"Login";
        String result = "";
        try{
            URL url = new URL(address);//初始化URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");//请求方式

            //超时信息
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);

            //post方式不能设置缓存，需手动设置为false
            conn.setUseCaches(false);

            //我们请求的数据
            String data = "username="+ URLEncoder.encode(username,"UTF-8")+
                    "&password="+ URLEncoder.encode(password,"UTF-8");

            //获取输出流
            OutputStream out = conn.getOutputStream();

            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();

            if (conn.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                result = new String(message.toByteArray());
                //return result;
            }
            else{
                Toast.makeText(LoginActivity.this, "网络连接出错！", Toast.LENGTH_SHORT).show();
            }

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }
}
