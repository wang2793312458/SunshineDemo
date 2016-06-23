package com.feicui.sunshinedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feicui.sunshinedemo.bean.WeekWeatherinfobean;
import com.feicui.sunshinedemo.utils.Constants;
import com.feicui.sunshinedemo.utils.Httputils;
import com.feicui.sunshinedemo.utils.ImageUtils;
import com.feicui.sunshinedemo.utils.Jsonutils;
import com.feicui.sunshinedemo.utils.SpUtils;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mWeather, mTem, mWind, mPower, mDateTime, mHum, mCityName;
    private ImageView mIv, mCity;
    private RelativeLayout mLayout;
    private List<WeekWeatherinfobean> data;
    private ListView mList;
    private static final int MSG = 1;
    private String city;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG:
                    mCityName.setText(Jsonutils.getRealTime().city_name);
                    mWind.setText(Jsonutils.getRealTime().direct);//风向
                    mPower.setText(Jsonutils.getRealTime().power);//风级
                    mDateTime.setText(Jsonutils.getRealTime().time);//更新时间
                    mHum.setText(Jsonutils.getRealTime().humidity + "%");//湿度
                    mWeather.setText(Jsonutils.getRealTime().info);//天气
                    mTem.setText(Jsonutils.getRealTime().temperature + "℃");//温度
                    mIv.setImageResource(ImageUtils.getImageDay(Jsonutils.getRealTime().id));
                    WeatherInfoAdapter adapter = new WeatherInfoAdapter(MainActivity.this, data);
                    mList.setAdapter(adapter);
                    break;
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initview() {
        mWeather = (TextView) findViewById(R.id.tv_weather_today);
        mTem = (TextView) findViewById(R.id.tv_tem_today);
        mWind = (TextView) findViewById(R.id.tv_wind_today);
        mPower = (TextView) findViewById(R.id.tv_power_today);
        mDateTime = (TextView) findViewById(R.id.tv_datetime);
        mHum = (TextView) findViewById(R.id.tv_humidity);
        mList = (ListView) findViewById(R.id.lv_week);
        mCityName = (TextView) findViewById(R.id.tv_city);
        mIv = (ImageView) findViewById(R.id.iv_weatherpic);
        mCity = (ImageView) findViewById(R.id.iv_city);
        mLayout = (RelativeLayout) findViewById(R.id.rl_today);
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TodayWeatherActivity.class);
                startActivity(intent);
            }
        });
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, WeekWeatherInfoActivity.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });
        new Thread(){
            @Override
            public void run() {
                String json= Httputils.getRequest("济南");
                Jsonutils.getWeatherDataFromJson(json);
                Jsonutils.getRealTime();
                data=Jsonutils.getJsonWeather();
                handler.sendEmptyMessage(MSG);
            }
        }.start();
    }
    private void weather(){
        new Thread(){
            @Override
            public void run() {
                city= SpUtils.getString(MainActivity.this, Constants.CITYNAME);
                String json=Httputils.getRequest(city);
                Jsonutils.getWeatherDataFromJson(json);
                Jsonutils.getRealTime();
                data=Jsonutils.getJsonWeather();
                handler.sendEmptyMessage(MSG);
            }
        }.start();
    }
    public void chooseCity(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        v=View.inflate(getApplicationContext(),R.layout.list_choosecity,null);
        final EditText etchoose= (EditText) v.findViewById(R.id.edt_choose);
        Button btn_login= (Button) v.findViewById(R.id.btn_login);
        builder.setView(v);
        final AlertDialog dialog=builder.show();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city=etchoose.getText().toString().trim();
                if (TextUtils.isEmpty(city)){
                    Toast.makeText(getApplicationContext(), "城市名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                SpUtils.putString(getApplicationContext(),Constants.CITYNAME,city);
                dialog.dismiss();
                weather();
            }
        });


    }
}
