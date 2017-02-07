package com.example.jerryfive.weather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jerryfive.weather.R;
import com.example.jerryfive.weather.db.WeatherDB;
import com.example.jerryfive.weather.model.City;
import com.example.jerryfive.weather.model.County;
import com.example.jerryfive.weather.model.Province;
import com.example.jerryfive.weather.util.HttpCallbackListener;
import com.example.jerryfive.weather.util.HttpUtil;
import com.example.jerryfive.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends Activity {

    // 定义层次
    private static final int LEVEL_PROVINCE = 1;
    private static final int LEVEL_CITY = 2;
    private static final int LEVEL_COUNTY = 3;

    private ProgressDialog progressDialog;
    private TextView titleView;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private WeatherDB weatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    // 选中的省
    private Province selectedProvince;
    private City selectedCity;

    // 当前级别
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); // 要放在 set 之前
        setContentView(R.layout.activity_weather);

        listView = (ListView) findViewById(R.id.list_view);
        titleView = (TextView)findViewById(R.id.title_text);

        // 采用Android自带的ListView样式
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        weatherDB = WeatherDB.getInstance( this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 如果点的是省，则查询市，如果点的是市，则查询县数据
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    queryCity();
                } else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounty();
                }
            }
        });
        queryProvince();
    }

    // 查询省，从数据库中获取省数据，放到ListProvince中
    // 如果数据库中没数据，则从服务器中查找
    private void queryProvince(){
        provinceList = weatherDB.loadProvince();
        if(provinceList.size()>0){
            dataList.clear();
            for (Province province: provinceList){
                dataList.add(province.getProvince_name());
            }
            // 通知ListView，有数据变更了。
            adapter.notifyDataSetChanged();
            listView.setSelection(0); // 选中第一个
            titleView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else{
            Toast.makeText(this, "from server", Toast.LENGTH_SHORT).show();
            queryFromServer(null, "province");
        }
    }

    private void queryCity(){
        cityList = weatherDB.loadCity(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for (City city: cityList){
                dataList.add(city.getCity_name());
            }
            // 通知ListView，有数据变更了。
            adapter.notifyDataSetChanged();
            listView.setSelection(0); // 选中第一个
            titleView.setText(selectedProvince.getProvince_name());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvince_code(), "city");
        }
    }

    private void queryCounty(){
        countyList = weatherDB.loadCounty(selectedCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for (County county: countyList){
                dataList.add(county.getCounty_name());
            }
            // 通知ListView，有数据变更了。
            adapter.notifyDataSetChanged();
            listView.setSelection(0); // 选中第一个
            titleView.setText(selectedCity.getCity_name());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCity_code(), "county");
        }
    }

    // 从服务器中获取数据
    // code:省，市的Code， Type：表示省，市，县级别
    private void queryFromServer(final String code, final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            // 市，县名称获取
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }else {
            // 省名称获取
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                Log.d("weather", "onfinish");
                if("province".equals(type)){
                    // 如果是省数据，则用处理省数据的方式处理
                    Log.d("weather", "province data from web");
                    result = Utility.handleProvinceResponse(weatherDB, response);
                    Log.d("weather", "true");
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(weatherDB, response, selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(weatherDB, response, selectedCity.getId());
                }
                if (result){
                    // 如果有结果（就是说数据库中有结果了），则通过 Ui 线程返回处理的数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                // 在QueryProvince中还会调用从服务器获取
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCity();
                            }else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(WeatherActivity.this, "load error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // progress
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("load ... ");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY){
            queryCity();
        }else if(currentLevel == LEVEL_CITY){
            queryProvince();
        }else{
            finish();
        }
    }
}
