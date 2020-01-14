package com.tianran.trweatherapp;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    public static final String TAG="MainActivity";
    private static final int UPDATE_NOW_WEATHER=1;
    private static final int UPDATE_TODAY_WEATHER=2;
    private boolean isFirst=true;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    // title
    private String citynumber,cityname;
    private LinearLayout city_linear;
    private Timer nowTimer,todayTimer;
    private TextView city_name,weather_author;

    // weather_now
    private ImageView weather_img;
    private TextView realtime_temperature,realtime_weather,date_text,day_temperature,day_weather,windpower;

    //weather_forecast
    private LinearLayout forecastLayout;

    //weather_suggestion
    private TextView carWashText,sportText,comfortText,uvText,clothesText,coldText;

    private void setTimerTask(){
        nowTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run: nowTimer");
                Message message=new Message();
                message.what=UPDATE_NOW_WEATHER;
                mHandler.sendMessage(message);
            }
        },1000,30*60*1000);
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_NOW_WEATHER:
                    updateNowWeather();
                    updateTodayWeather();
                    updateForecastWeather();
                    updateLifestyleWeather();
                    break;
                    default:
                        break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Log.d("MainActivity","onCreate execute");

        // 测试网络状态
        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
            Log.d(TAG, "onCreate: 网络OK");
            nowTimer = new Timer();
            setTimerTask();

        } else {
            Log.d(TAG, "onCreate: 网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了",
                    Toast.LENGTH_SHORT).show();
        }

        initView();
        TimeThread timeThread=new TimeThread(date_text);
        timeThread.start();

        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);

    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            String city = location.getCity();    //获取城市
            Log.d(TAG, "MyLocationListener: "+city);
            SharedPreferences.Editor editor=getSharedPreferences("CityActivity",MODE_PRIVATE).edit();
            editor.putString("locatecity",city);
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        updateNowWeather();
        updateTodayWeather();
        updateForecastWeather();
        updateLifestyleWeather();
    }

    void initView(){
        // title
        city_linear=(LinearLayout)findViewById(R.id.city_linear);
        weather_author=(TextView)findViewById(R.id.author);
        city_name=(TextView)findViewById(R.id.title_city);
        city_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CityActivity.class);
                intent.putExtra("city",city_name.getText());
                startActivity(intent);

            }
        });
        if (!isFirst){
            SharedPreferences pref=getSharedPreferences("CityActivity",MODE_PRIVATE);
            cityname=pref.getString("locatecity","北京");
            citynumber=pref.getString("locatenumber","101011100");
            isFirst=true;
        }
        else{
            SharedPreferences pref=getSharedPreferences("CityActivity",MODE_PRIVATE);
            city_name.setText(pref.getString("city","北京"));
            citynumber=pref.getString("number","101011100");
        }

        // now
        weather_img=(ImageView)findViewById(R.id.weather_img);
        realtime_temperature=(TextView)findViewById(R.id.realtime_temperature);
        realtime_weather=(TextView)findViewById(R.id.realtime_weather);
        date_text=(TextView)findViewById(R.id.date_text);
        day_temperature=(TextView)findViewById(R.id.day_temperature);
        day_weather=(TextView)findViewById(R.id.day_weather);
        windpower=(TextView)findViewById(R.id.windpower);

        // forecast
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);

        // suggestion
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        uvText = (TextView)findViewById(R.id.uv_text);
        clothesText = (TextView)findViewById(R.id.clothes_text);
        coldText = (TextView)findViewById(R.id.cold_text);

    }


    private void updateNowWeather(){
        // 获得数据
        String param="location=CN"+citynumber+"&key=8c8a520cb0e343958d434471d3344645";
        String url= "https://free-api.heweather.net/s6/weather/now?";
        ConnThread connThread=new ConnThread(url,param);
        connThread.start();
        while(connThread.getRes()==null);
        String res=connThread.getRes();

        Log.d(TAG, "updateNowWeather: "+res);
        // 解析数据
        Gson gson=new Gson();
        WeatherBean nowWeather=gson.fromJson(res,WeatherBean.class);
        WeatherBean.HeWeather6Bean.NowBean now=nowWeather.getHeWeather6().get(0).getNow();
        String status=nowWeather.getHeWeather6().get(0).getStatus();
        if(status!=null)
            Log.d(TAG, "updateNowWeather: "+status);
        if(status.equals("unknown location")){
            Log.d(TAG, "updateNowWeather: ChangeCity");
            SharedPreferences.Editor editor=getSharedPreferences("CityActivity",MODE_PRIVATE).edit();
            editor.putString("city","北京");
            editor.putString("number","101011100");
            editor.apply();
            initView();
            updateNowWeather();
            updateTodayWeather();
            updateForecastWeather();
            updateLifestyleWeather();
        }

        //更新UI
        realtime_temperature.setText(now.getTmp());
        realtime_weather.setText(getString(R.string.realtime_weather,now.getCond_txt()));
        int code= Integer.parseInt(now.getCond_code());
        switch (code){
            case 100:
            case 201:
                weather_img.setImageResource(R.drawable.qingtian);
                break;
            case 101:
            case 102:
            case 103:
                weather_img.setImageResource(R.drawable.duoyun);
                break;
            case 104:
                weather_img.setImageResource(R.drawable.yintian);
                break;
            case 200:
            case 202:
            case 203:
            case 204:
            case 503:
                weather_img.setImageResource(R.drawable.yangsha);
                break;
            case 205:
            case 206:
            case 207:
            case 208:
            case 209:
            case 507:
            case 508:
                weather_img.setImageResource(R.drawable.shachenbao);
                break;
            case 210:
            case 211:
            case 212:
            case 213:
                weather_img.setImageResource(R.drawable.longjuanfeng);
                break;
            case 300:
            case 301:
                weather_img.setImageResource(R.drawable.zhenyu);
                break;
            case 302:
            case 303:
            case 304:
                weather_img.setImageResource(R.drawable.leizhenyu);
                break;
            case 305:
            case 309:
            case 314:
                weather_img.setImageResource(R.drawable.xiaoyu);
                break;
            case 306:
            case 315:
            case 319:
                weather_img.setImageResource(R.drawable.zhongyu);
                break;
            case 307:
            case 308:
            case 310:
            case 311:
            case 312:
            case 316:
            case 317:
            case 318:
                weather_img.setImageResource(R.drawable.dayu);
                break;
            case 313:
                weather_img.setImageResource(R.drawable.bingbao);
                break;
            case 400:
            case 408:
                weather_img.setImageResource(R.drawable.xiaoxue);
                break;
            case 401:
            case 409:
            case 499:
                weather_img.setImageResource(R.drawable.zhongxue);
                break;
            case 402:
            case 403:
            case 410:
                weather_img.setImageResource(R.drawable.daxue);
                break;
            case 404:
            case 405:
            case 406:
                weather_img.setImageResource(R.drawable.yujiaxue);
                break;
            case 407:
                weather_img.setImageResource(R.drawable.zhenxue);
                break;
            case 500:
            case 501:
            case 509:
            case 510:
                weather_img.setImageResource(R.drawable.wu);
                break;
            case 502:
            case 511:
            case 512:
            case 513:
                weather_img.setImageResource(R.drawable.mai);
                break;
            case 504:
                weather_img.setImageResource(R.drawable.fuchen);
                break;
                default:
                    break;
        }
    }

    private void updateTodayWeather() {
        // 获取数据
        String param="location=CN"+citynumber+"&key=8c8a520cb0e343958d434471d3344645";
        String url = "https://free-api.heweather.net/s6/weather/forecast?";
        ConnThread connThread=new ConnThread(url,param);
        connThread.start();
        while(connThread.getRes()==null);
        String res=connThread.getRes();
        Log.d(TAG, "updateTodayWeather: "+res);

        // 解析数据
        Gson gson=new Gson();
        TodayWeatherBean todayWeather=gson.fromJson(res,TodayWeatherBean.class);
        TodayWeatherBean.HeWeather6Bean.DailyForecastBean daily=todayWeather.getHeWeather6().get(0).getDaily_forecast().get(0);
        String status = todayWeather.getHeWeather6().get(0).getStatus();
        if (status != null)
            Log.d(TAG, "updateTodayWeather: " + status);

        // 更新UI
        day_temperature.setText(getString(R.string.day_temperature,daily.getTmp_min(),daily.getTmp_max()));
        windpower.setText(getString(R.string.windpower,daily.getWind_dir(),daily.getWind_sc()));
        if(daily.getCond_txt_d().equals(daily.getCond_txt_n())){
            day_weather.setText(daily.getCond_txt_d());
        }
        else {
            day_weather.setText(getString(R.string.day_weather,daily.getCond_txt_d(),daily.getCond_txt_n()));
        }
    }

    private void updateForecastWeather(){
        // 获得数据https://free-api.heweather.net/s6/weather/hourly?location=CN101011100&key=8c8a520cb0e343958d434471d3344645
        String param="location=CN"+citynumber+"&key=8c8a520cb0e343958d434471d3344645";
        String url= "https://free-api.heweather.net/s6/weather/forecast?";
        ConnThread connThread=new ConnThread(url,param);
        connThread.start();
        while(connThread.getRes()==null);
        String res=connThread.getRes();

        Log.d(TAG, "updateForecastWeather: "+res);
        // 解析数据
        Gson gson=new Gson();
        ForecastBean foreWeather=gson.fromJson(res,ForecastBean.class);
        List<ForecastBean.HeWeather6Bean.DailyForecastBean> forecastList=foreWeather.getHeWeather6().get(0).getDaily_forecast();
        String status=foreWeather.getHeWeather6().get(0).getStatus();
        if(status!=null)
            Log.d(TAG, "updateForecastWeather: "+status);
//        if(status.equals("unknown location")){
//            Log.d(TAG, "updateNowWeather: ChangeCity");
//            SharedPreferences.Editor editor=getSharedPreferences("CityActivity",MODE_PRIVATE).edit();
//            editor.putString("city","北京");
//            editor.putString("number","101011100");
//            editor.apply();
//            initView();
//            updateNowWeather();
//            updateTodayWeather();
//            updateForecastWeather();
//            updateLifestyleWeather();
//        }

        //更新UI
        forecastLayout.removeAllViews();
        for (ForecastBean.HeWeather6Bean.DailyForecastBean forecast: forecastList){
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.weather_forecast_item, forecastLayout, false);
            TextView dateText = (TextView)view.findViewById(R.id.data_text);  // 日期
            TextView infoText = (TextView)view.findViewById(R.id.info_text);  // 天气
            TextView maxMinText = (TextView)view.findViewById(R.id.max_min_text);  // 温度

            dateText.setText(forecast.getDate());
            String infoD=forecast.getCond_txt_d();
            String infoN=forecast.getCond_txt_n();
            if(infoD.equals(infoN)){
                infoText.setText(infoD);
            }
            else{
                infoText.setText(infoD+"转"+infoN);
            }
            maxMinText.setText(forecast.getTmp_min() + " ～ " + forecast.getTmp_max());
            forecastLayout.addView(view);
        }
    }

    private void updateLifestyleWeather(){
        String param="location=CN"+citynumber+"&key=8c8a520cb0e343958d434471d3344645";
        String url= "https://free-api.heweather.net/s6/weather/lifestyle?";
        ConnThread connThread=new ConnThread(url,param);
        connThread.start();
        while(connThread.getRes()==null);
        String res=connThread.getRes();

        Log.d(TAG, "updateLifestyleWeather: "+res);
        // 解析数据
        Gson gson=new Gson();
        WeaLifestyleBean lifeWeather=gson.fromJson(res,WeaLifestyleBean.class);
        List<WeaLifestyleBean.HeWeather6Bean.LifestyleBean> lifestyleList=lifeWeather.getHeWeather6().get(0).getLifestyle();
        String status=lifeWeather.getHeWeather6().get(0).getStatus();
        if(status!=null)
            Log.d(TAG, "updateLifestyleWeather: "+status);

        //更新UI
        for(WeaLifestyleBean.HeWeather6Bean.LifestyleBean lifestyle: lifestyleList){
            String type=lifestyle.getType();
            String brf=lifestyle.getBrf();
            if(type.equals("comf")){
                comfortText.setText(brf);
                comfortText.getPaint().setFakeBoldText(true);
            }
            else if(type.equals("cw")){
                carWashText.setText(brf);
                carWashText.getPaint().setFakeBoldText(true);
            }
            else if(type.equals("sport")){
                sportText.setText(brf);
                sportText.getPaint().setFakeBoldText(true);
            }
            else if(type.equals("sport")){
                sportText.setText(brf);
                sportText.getPaint().setFakeBoldText(true);
            }
            else if(type.equals("uv")){
                uvText.setText(brf);
                uvText.getPaint().setFakeBoldText(true);
            }
            else if(type.equals("drsg")){
                clothesText.setText(brf);
                clothesText.getPaint().setFakeBoldText(true);
            }
            else if(type.equals("drsg")){
                clothesText.setText(brf);
                clothesText.getPaint().setFakeBoldText(true);
            }
            else if(type.equals("flu")){
                coldText.setText(brf);
                coldText.getPaint().setFakeBoldText(true);
            }
        }
    }
}
