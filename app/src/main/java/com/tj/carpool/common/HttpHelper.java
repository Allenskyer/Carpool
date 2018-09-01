package com.tj.carpool.common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {
    private String result;
    public String getResult() {
        return result;
    }
    public boolean ServletPost(String address, JSONObject jsonObject) throws IOException, JSONException {
        URL url = new URL(address);//初始化URL
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");//请求方式

        //超时信息
        conn.setReadTimeout(5000);
        conn.setConnectTimeout(5000);

        //post方式不能设置缓存，需手动设置为false
        conn.setUseCaches(false);

        //获取输出流
        OutputStream out = conn.getOutputStream();

        out.write(jsonObject.toString().getBytes());
        out.flush();
        out.close();
        conn.connect();

        int responceCode = conn.getResponseCode();
        if (responceCode == 200) {
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream message = new ByteArrayOutputStream();
            int len = 0;
            byte buffer[] = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                message.write(buffer, 0, len);
            }
            is.close();
            message.close();
            result = new String(message.toByteArray());
        } else {
            return false;
        }
        return true;
    }
}
