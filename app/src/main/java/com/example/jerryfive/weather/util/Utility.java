package com.example.jerryfive.weather.util;

import android.text.TextUtils;

import com.example.jerryfive.weather.db.WeatherDB;
import com.example.jerryfive.weather.model.City;
import com.example.jerryfive.weather.model.County;
import com.example.jerryfive.weather.model.Province;

/**
 * Created by jerryfive on 2017/2/6.
 * 用于处理返回数据，使其满足数据库的要求
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
}
