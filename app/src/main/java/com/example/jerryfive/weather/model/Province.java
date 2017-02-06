package com.example.jerryfive.weather.model;

/**
 * Created by jerryfive on 2017/2/6.
 *
 * 省数据的类
 */

public class Province {

    private Integer id;
    private String province_name;
    private String province_code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getProvince_code() {
        return province_code;
    }

    public void setProvince_code(String province_code) {
        this.province_code = province_code;
    }
}
