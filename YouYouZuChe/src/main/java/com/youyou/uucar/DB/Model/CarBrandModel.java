package com.youyou.uucar.DB.Model;

import org.json.JSONArray;
import org.json.JSONObject;

public class CarBrandModel
{
    private String name; // 显示的数据
    private String sortLetters; // 显示数据拼音的首字母
    public String xinghao = "";

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSortLetters()
    {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters)
    {
        this.sortLetters = sortLetters;
    }

    public void setJson(JSONArray json)
    {
        this.json = json;
    }

    public JSONArray getJson()
    {
        return json;
    }

    JSONArray json;
}
