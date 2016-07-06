package com.example.mosr.pullwebxml;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xutils.common.Callback;
import org.xutils.x;

import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private static String url = "http://ws.webxml.com.cn/WebServices/WeatherWebService.asmx/getWeatherbyCityName";
    private static String parameter = "theCityName";
    private static String city = "南京";
    private StringBuilder mBulider;
    private TextView mtv;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            mtv.setText(msg.what == 1 ? msg.obj.toString().contains("访问被限制！") ? "请稍候重试" : msg.obj.toString() : "请求失败");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.Ext.init(this.getApplication());
        x.Ext.setDebug(true);
        mtv = (TextView) findViewById(R.id.mtv);
        mtv.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                methodOne();
            }
        });
        findViewById(R.id.Button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                methodTwo(city, callback);
            }
        });
    }

    private void methodOne() {
        mBulider = new StringBuilder();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL u = new URL(url + "?" + parameter + "=" + URLEncoder.encode(city, "utf-8"));
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
                        changeUI();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 天气查询接口
    public void methodTwo(String theCityName, Callback.CommonCallback<String> callback) {
        org.xutils.http.RequestParams mpaParams = new org.xutils.http.RequestParams(url);
        mpaParams.addBodyParameter(parameter, city);
        x.http().get(mpaParams, callback);
    }

    Callback.CommonCallback<String> callback = new Callback.CommonCallback<String>() {

        @Override
        public void onCancelled(CancelledException arg0) {
        }

        @Override
        public void onError(Throwable arg0, boolean arg1) {
        }

        @Override
        public void onFinished() {
        }

        @Override
        public void onSuccess(String arg0) {
            try {
                mBulider = new StringBuilder();
                // InputStream is = StrToInput.getXml(arg0);
                XmlPullParser pullParser = Xml.newPullParser();
                StringReader reader = new StringReader(arg0);
                pullParser.setInput(reader);
                int event = pullParser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG:

                            if ("string".equals(pullParser.getName()))
                                mBulider.append(pullParser.nextText().trim().toString() + "\n");
                            break;
                        case XmlPullParser.END_TAG:
                            // if ("new".equals(pullParser.getName())) {
                            //
                            // wearthList = null;
                            // }
                            break;
                    }
                    event = pullParser.next();
                }
                // reader.close();
                changeUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void changeUI() {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = mBulider;
        mHandler.sendMessage(msg);
        Log.d("scw", mBulider.toString());
    }

}
