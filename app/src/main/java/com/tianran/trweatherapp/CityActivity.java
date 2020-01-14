package com.tianran.trweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CityActivity extends AppCompatActivity {

    private static final String TAG="CityActivity";

    private ImageView back_main;
    private ListView city_list;
    private TextView city_name_now,locate_city;
    private CityDB mCityDB;
    private List<CityBean> mCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_city);
        city_name_now=(TextView)findViewById(R.id.city_name_now);
        SharedPreferences pref=getSharedPreferences("CityActivity",MODE_PRIVATE);
        city_name_now.setText(getString(R.string.city_name_now,pref.getString("city","XX")));
        back_main=(ImageView)findViewById(R.id.back_main);
        back_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCityDB=openCityDB();
        initCityList();

        locate_city=(TextView)findViewById(R.id.locate_city_name);
        locate_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onItemClick: locate_city");
                String city=locate_city.getText().toString();
                city_name_now.setText(getString(R.string.city_name_now,city));
                SharedPreferences.Editor editor=getSharedPreferences("CityActivity",MODE_PRIVATE).edit();
                editor.putString("city",city);
                editor.putString("number","101011100");
                editor.apply();
            }
        });


    }
    private void initCityList(){
        city_list=(ListView)findViewById(R.id.city_list);
        mCityList=mCityDB.getAllCity();
        Log.d(TAG, "initCityList: "+mCityList.toString());
        CityAdapter cityAdapter=new CityAdapter(
                CityActivity.this,R.layout.city_item,mCityList);
        city_list.setAdapter(cityAdapter);
        city_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityBean cityBean=mCityList.get(position);
                String city=cityBean.getCity();
                Log.d(TAG, "onItemClick: selectcity"+city);
                city_name_now.setText(getString(R.string.city_name_now,city));
                SharedPreferences.Editor editor=getSharedPreferences("CityActivity",MODE_PRIVATE).edit();
                editor.putString("city",city);
                editor.putString("number",cityBean.getNumber());
                editor.apply();
            }
        });

    }

    private CityDB openCityDB(){
        String path="/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator+getPackageName()
                + File.separator+"database1"
                +File.separator
                +CityDB.CITY_DB_NAME;
        File db=new File(path);
        Log.d(TAG, "openCityDB: "+path);
        if(!db.exists()){
            String pathfolder="/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    +File.separator+getPackageName()
                    + File.separator+"database1"
                    +File.separator;
            File disFirstFolder=new File(pathfolder);
            if(!disFirstFolder.exists()){
                disFirstFolder.mkdirs();
                Log.i(TAG, "openCityDB: mkdirs");
            }
            Log.i(TAG, "openCityDB: db is not exists");
            try{
                InputStream is=getAssets().open("city.db");
                FileOutputStream fos=new FileOutputStream(db);
                int len=-1;
                byte[] buffer=new byte[1024];
                while ((len=is.read(buffer))!=-1){
                    fos.write(buffer,0,len);
                    fos.flush();
                }
                fos.close();
                is.close();
            }catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this,path);
    }
}
