package com.example.myesv6;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


import com.example.model.ResponseObj;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jay on 2016/1/2 0002.
 */
public class HttpHelper {
    private SharedPreferences sharedPreferences;

    public HttpHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public ResponseObj get(String url, HashMap<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        String token = sharedPreferences.getString("token", "");
        if (!token.equals("")) {
            params.put("token", token);
        }
        HttpURLConnection connection = null;
        String ret = "";
        url += "?";
        int code = -1;
        try {
            Iterator iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                url += key + "=" + val + "&";
            }
            connection = (HttpURLConnection) ((new URL(url).openConnection()));
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(40000);
            connection.setReadTimeout(40000);
            code = connection.getResponseCode();
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            ret = response.toString();
        } catch (Exception e) {
            ret = e.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return new ResponseObj(code, ret);
        }
    }

    public ResponseObj post(String url, HashMap<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        String token = sharedPreferences.getString("token", "");
        if (!token.equals("")) {
            params.put("token", token);
        }
        HttpURLConnection connection = null;
        String ret = "";
        int code = -1;
        try {
            connection = (HttpURLConnection) ((new URL(url).openConnection()));
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(40000);
            connection.setReadTimeout(40000);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            Iterator iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                out.writeBytes(key + "=" + val + "&");
            }
            code = connection.getResponseCode();
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            ret = response.toString();
        } catch (Exception e) {
            ret = e.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return new ResponseObj(code, ret);
        }
    }
}

