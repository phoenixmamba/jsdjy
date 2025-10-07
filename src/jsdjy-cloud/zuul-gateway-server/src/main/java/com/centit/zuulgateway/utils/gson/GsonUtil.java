package com.centit.zuulgateway.utils.gson;

import com.centit.zuulgateway.po.PageData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @描述：
 * @作者： zhouchaoxi
 * @日期：2018/8/7
 */
public class GsonUtil {

    public static void main(String[] args) {
        System.out.println(jsonToPageData("{\"user_id\":1.0,\"type\":0,\"datetoken\":\"2020-1-15 17:24:43\"}"));
    }

    public static PageData jsonToPageData(String json) {
        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<PageData>() {
        }.getType(), new GsonType()).create();
        PageData map = gson.fromJson(json, PageData.class);
        return map;
    }

    public static Map jsonToMap(String json) {
        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<PageData>() {
        }.getType(), new GsonType()).create();
        HashMap map = gson.fromJson(json, HashMap.class);
        return map;
    }

    public static List<PageData> jsonToPageDataList(String json) {
        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<ArrayList<PageData>>() {
        }.getType(), new GsonType()).create();
        List<PageData> map = gson.fromJson(json, ArrayList.class);
        return map;
    }


}
