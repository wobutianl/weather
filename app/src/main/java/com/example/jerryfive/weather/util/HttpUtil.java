package com.example.jerryfive.weather.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jerryfive on 2017/2/6.
 * 通过网络获取省，市，县o 数据
 */

public class HttpUtil {

    // 传入URL + 监听器
    public static void sendHttpRequest(final String address,
                                       final HttpCallbackListener listener){
        // 在线程中处理网络事件
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 新建Http连接
                HttpURLConnection connection = null;
                try{
                    // 把字符串转为URL
                    URL url = new URL(address);
                    // 打开连接
                    connection = (HttpURLConnection)url.openConnection();
                    // 通过Get的方式获取返回数据
                    connection.setRequestMethod("GET");
                    // 设置超时时间
                    connection.setConnectTimeout(8000);
                    // 设置读取超时时间
                    connection.setReadTimeout(8000);
                    //
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    // 字符串流，用于存储传回的数据
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        response.append(line);
                    }
                    if(listener!=null){
                        // 如果有监听者，那么把获取的数据传给监听者
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e){
                    if(listener!=null){
                        listener.onError(e);
                    }
                }finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        });
    }
}
