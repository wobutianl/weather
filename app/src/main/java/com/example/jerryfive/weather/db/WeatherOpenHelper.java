package com.example.jerryfive.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.camera2.params.StreamConfigurationMap;

/**
 * Created by jerryfive on 2017/2/6.
 * 创建城市所需要的表（省，市，县）
 * 思路：
 * 先写创建数据表的SQL语句
 * 在Create方法中调用创建语句
 *
 * 以后要更新的地方：在Update中创建天气记录表
 */


public class WeatherOpenHelper extends SQLiteOpenHelper {

    private final String CreateProvince = "create table Province(" +
            "id integer primary key autoincrement, " +
            "province_name text, " +
            "province_code text )";

    private final String CreateCity = "create table City(" +
            "id integer primary key autoincrement, " +
            "city_name text, " +
            "city_code text, " +
            "province_id integer )";  // 这里应该设置外键

    private final String CreateCounty = "create table County (" +
            "id integer primary key autoincrement, " +
            "county_name text, " +
            "county_code text, " +
            "city_id integer )"; // 这里设置外键

    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                             int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 只在第一次调用时执行
        sqLiteDatabase.execSQL(CreateProvince);
        sqLiteDatabase.execSQL(CreateCity);
        sqLiteDatabase.execSQL(CreateCounty);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
