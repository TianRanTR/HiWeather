package com.tianran.trweatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CityAdapter extends ArrayAdapter<CityBean> {

    private int resID;

    public CityAdapter(Context context, int textViewID, List<CityBean> obj){
        super(context,textViewID,obj);
        resID=textViewID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        CityBean cityBean=getItem(position);
        View view;
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(resID,parent,false);
        }else {
            view = convertView;
        }
        TextView cityName=(TextView)view.findViewById(R.id.city_item);
        TextView cityCode=(TextView)view.findViewById(R.id.city_code);
        cityName.setText(cityBean.getCity());
        cityCode.setText(cityBean.getNumber());
        return view;
    }
}

