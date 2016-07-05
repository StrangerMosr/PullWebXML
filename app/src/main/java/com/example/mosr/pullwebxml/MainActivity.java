package com.example.mosr.pullwebxml;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.xmlpull.v1.XmlPullParser;

import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static String url = "http://ws.webxml.com.cn/WebServices/WeatherWebService.asmx/getWeatherbyCityName?theCityName=";
    private static String city = "南京";
    private StringBuilder mBulider;
    private TextView mtv;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            mtv.setText(msg.what == 1 ? msg.obj.toString() : "请求失败");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtv = (TextView) findViewById(R.id.mtv);
        mtv.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                methodOne();
            }
        });
    }

    private void methodOne() {
        mBulider = new StringBuilder();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL u = new URL(url + URLEncoder.encode(city, "utf-8"));
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    // conn.setDoInput(true);
                    // conn.setDoOutput(true);
                    conn.setRequestMethod("GET");
                    // conn.setUseCaches(true);
                    // conn.setRequestProperty("Content-Type",
                    // "application/x-www-form-url");
                    // conn.setRequestProperty("Charset", "utf-8");
                    // conn.connect();
                    if (conn.getResponseCode() == 200) {
                        InputStream in = conn.getInputStream();
                        XmlPullParser xmlPullParser = Xml.newPullParser();
                        xmlPullParser.setInput(in, "UTF-8");
                        int eventCode = xmlPullParser.getEventType();
                        while (eventCode != XmlPullParser.END_DOCUMENT) {
                            String name = xmlPullParser.getName();
                            switch (eventCode) {
                                case XmlPullParser.START_DOCUMENT:// 文档开始

                                    break;
                                case XmlPullParser.START_TAG:// 元素开始.
                                    if (name.equalsIgnoreCase("string"))
                                        mBulider.append(xmlPullParser.nextText().trim().toString() + "\n");
                                    break;
                                case XmlPullParser.END_TAG:// 元素结束

                                    break;
                                case XmlPullParser.END_DOCUMENT:// 文档结束

                                    break;

                                default:
                                    break;
                            }
                            eventCode = xmlPullParser.next();
                        }
                        in.close();
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = mBulider;
                        mHandler.sendMessage(msg);
                        Log.d("scw", mBulider.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
