package com.tianran.trweatherapp;

public class CityBean {
    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFirstPY;


    public CityBean(String province,String city,String number,
                String firstPY,String allPY,String allFirstPY){
        this.allFirstPY=allFirstPY;
        this.allPY=allPY;
        this.city=city;
        this.firstPY=firstPY;
        this.number=number;
        this.province=province;
    }

    public String getAllFirstPY() {
        return allFirstPY;
    }

    public String getAllPY() {
        return allPY;
    }

    public String getCity() {
        return city;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public String getNumber() {
        return number;
    }

    public String getProvince() {
        return province;
    }
}
