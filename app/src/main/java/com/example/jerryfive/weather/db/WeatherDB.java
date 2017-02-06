package com.example.jerryfive.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jerryfive.weather.model.City;
import com.example.jerryfive.weather.model.County;
import com.example.jerryfive.weather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerryfive on 2017/2/6.\
 * 用于操作数据表，增，查
 * 采用单例模式，
 *
 */

public class WeatherDB {

    // 创建DB需要的参数：数据库名，版本号，

    public static final String db_name = "weather";
    public static final Integer db_version = 1;

    private static WeatherDB weatherDB; // 自身，只有一个实例

    private SQLiteDatabase db ; // 数据库对象

    public WeatherDB(Context context){
        WeatherOpenHelper dbHelper = new WeatherOpenHelper(context, db_name, null, db_version);
        db = dbHelper.getWritableDatabase(); // 以可写的方式创建数据库
    }

    // 单例
    public synchronized static WeatherDB getInstance(Context context){
        if (weatherDB != null){
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    // 保存数据到数据库（省数据需要：省名，省Code）
    public void saveProvince(Province province){
        if(province != null){
            ContentValues value = new ContentValues();
            value.put("province_name", province.getProvince_name());
            value.put("province_code", province.getProvince_code());
            db.insert("Province", null, value);
        }
    }

    // 获取所有省数据，用于填充ListView（省数据，查询省的表）
    public List<Province> loadProvince(){
        List<Province> list = new ArrayList<Province>();

        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvince_name(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvince_code(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        // 不要忘记关闭Cursor
        if (cursor!= null){
            cursor.close();
        }
        return list;
    }

    // City 保存与获取
    public void saveCity(City city){
        if(city != null){
            ContentValues value = new ContentValues();
            value.put("city_name", city.getCity_name());
            value.put("city_code", city.getCity_code());
            value.put("province_id", city.getProvince_id());
            db.insert("City", null, value);
        }
    }

    // 获取所有市数据，用于填充ListView
    public List<City> loadCity(int province_id){
        List<City> list = new ArrayList<City>();

        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(province_id)}
                , null, null, null);
        if(cursor.moveToFirst()){
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvince_id(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            }while (cursor.moveToNext());
        }
        // 不要忘记关闭Cursor
        if (cursor!= null){
            cursor.close();
        }
        return list;
    }

    // 县数据
    public void saveCounty(County county
    ){
        if(county != null){
            ContentValues value = new ContentValues();
            value.put("county_name", county.getCounty_name());
            value.put("county_code", county.getCounty_code());
            value.put("City_id", county.getCity_id());
            db.insert("County", null, value);
        }
    }

    // 获取所有市数据，用于填充ListView
    public List<County> loadCounty(int city_id){
        List<County> list = new ArrayList<County>();

        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(city_id)}
                , null, null, null);
        if(cursor.moveToFirst()){
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCounty_name(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCounty_code(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCity_id(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            }while (cursor.moveToNext());
        }
        // 不要忘记关闭Cursor
        if (cursor!= null){
            cursor.close();
        }
        return list;
    }
}
