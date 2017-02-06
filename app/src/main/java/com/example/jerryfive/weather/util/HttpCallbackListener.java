package com.example.jerryfive.weather.util;

/**
 * Created by jerryfive on 2017/2/6.
 */

public interface HttpCallbackListener{
    void onFinish(String response);

    void onError(Exception e);
}