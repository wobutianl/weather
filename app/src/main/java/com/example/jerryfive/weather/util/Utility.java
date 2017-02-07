package com.example.jerryfive.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.jerryfive.weather.db.WeatherDB;
import com.example.jerryfive.weather.model.City;
import com.example.jerryfive.weather.model.County;
import com.example.jerryfive.weather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jerryfive on 2017/2/6.
 * 用于处理返回数据，使其满足数据库的要求
 * 处理天气信息的Json方法
 * 将处理后的数据存储到SharedReference文件中4
 */

public class Utility {

    // 解析省数据, 并存储到数据库中
    public synchronized static boolean handleProvinceResponse(WeatherDB weatherDB,
                                                              String response){
        if( !TextUtils.isEmpty(response)){
            // 以 | 来切分数据
            String[] allProvince = response.split(",");
            if(allProvince != null && allProvince.length > 0) {
                for (String p : allProvince) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvince_code(array[0]);
                    province.setProvince_name(array[1]);
                    // 将解析出来的数据，保存到数据库中
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    // 解析市数据
    // 需要知道省的ID
    public static boolean handleCityResponse(WeatherDB weatherDB,
                                             String response, int province_id){
        //
        if(!TextUtils.isEmpty(response)){
            String[] allCity = response.split(",");
            if(allCity!=null && allCity.length>0){
                for( String c : allCity){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setProvince_id(province_id);
                    city.setCity_name(array[1]);
                    city.setCity_code(array[0]);
                    // save to table
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    // 解析县数据
    public static boolean handleCountyResponse(WeatherDB weatherDB,
                                             String response, int city_id){
        //
        if(!TextUtils.isEmpty(response)){
            String[] allCounty = response.split(",");
            if(allCounty!=null && allCounty.length>0){
                for( String c : allCounty){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCity_id(city_id);
                    county.setCounty_name(array[1]);
                    county.setCounty_code(array[0]);
                    // save to table
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    // 解析Json格式数据，
    public static void handleWeatherResponse(Context context, String response){
        try{
            // 新建一个 Json 对象，传入Json 文件
            JSONObject jsonObject = new JSONObject(response);
            // 获取名称为 weatherinfo 的Json数据
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            // 获取Json中名称为 City的数据
            String cityName = weatherInfo.getString("city");
            //
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    // 将处理的数据存储到 SharedReference中
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode,String temp1,
                                       String temp2, String weatherDesp, String publishTime){
        // 利用SharedReference.Editor 来存储数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));

        editor.commit();
    }
}
