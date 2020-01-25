package com.coolweather.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 如何在活动中请求天气数据
 * 如何将数据展示到界面上
 */
public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;//城市名

    private TextView titleUpdateTime;//更新时间

    private  TextView degreeText;//当前气温

    private TextView weatherInfoText;//显示天气概况

    private LinearLayout forecastLayout;//未来几天天气信息

    private TextView aqiText;//aqi指数

    private TextView pm25Text;//pm2.5指数

    private TextView comfortText;//舒适建议

    private TextView carWashText;//洗车建议

    private TextView sportText;//运动建议

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化各控件
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text=findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        sportText = findViewById(R.id.sport_text);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString !=null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else {
            //无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    /**
     * 根据天气id请求城市天气信息
     * @param weatherId
     */
    private void requestWeather(final String weatherId){

        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        //调用HttpUtil.sendOkHttpRequest（）向该地址发出请求，服务器会将相应城市的天气信息以JSON格式返回
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                //将返回的JSON数据转换成Weather对象
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //再将当前线程切换到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //然后进行判断 如果服务器返回的status状态是ok 就说明请求天气成功了
                        //此时返回的数据缓存到SharedPreferences当中
                        //并调用showWeatherInfo()方法来进行内容展示
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                    WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    /**
     * 处理并展示Weather实体类中的数据
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        //从weather对象中获取数据 然后显示到相应的控件上
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+"C";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        //未来几天的天气预报部分使用了一个for循环来处理每天的天气信息，
        // 在循环中动态加载forecast_item.xml布局并设置相应数据，然后添加到父布局当中
        for (Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,
                    false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            minText.setText(forecast.temperature.min);
            maxText.setText(forecast.temperature.max);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度: " +weather.suggestion.comfort.info;
        String carWash = "洗车指数" + weather.suggestion.carWash.info;
        String sport = "运动建议:" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        comfortText.setText(carWash);
        comfortText.setText(sport);
        //设置完所有数据后 将ScrollView重新变成可见
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
