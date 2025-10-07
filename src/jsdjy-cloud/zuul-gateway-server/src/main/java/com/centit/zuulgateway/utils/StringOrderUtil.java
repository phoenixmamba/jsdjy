package com.centit.zuulgateway.utils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class StringOrderUtil {
    public static boolean isScrambledString(String source,String target){
        if(source==null||target==null){
            throw new IllegalArgumentException("source or target is null.");
        }

        if(source.length()!=target.length()){
            System.out.println("target string's length is not equal to source length.");
            return false;
        }

        //目标串中每个字符都包含在原串中
        int length = source.length();
        Map<Character,Integer> targetCount = new HashMap<Character,Integer>();
        for(int i =0;i<length;i++){
            char c = target.charAt(i);
            //target中某个字符不在原串中，返回false
            int indexOfSource = source.indexOf(c);
            if(indexOfSource==-1){
                return false;
            }

            //统计该串在本串中的个数
            if(targetCount.get(c)==null){
                targetCount.put(c, 1);
            }else{
                Integer count = targetCount.get(c);
                targetCount.put(c, 1+count);
            }
        }

        //统计原串中各个字符的个数
        Map<Character,Integer> sourceCount = new HashMap<Character,Integer>();
        for(int i =0;i<length;i++){
            char c = source.charAt(i);
            if(sourceCount.get(c)==null){
                sourceCount.put(c, 1);
            }else{
                Integer count = sourceCount.get(c);
                sourceCount.put(c, 1+count);
            }
        }

        //目标串中每个字符个数跟原串中对应字符的个数一样
        for(Map.Entry<Character, Integer> entry:targetCount.entrySet()){
            Character key = entry.getKey();
            if(entry.getValue()!=sourceCount.get(key)){
                return false;
            }
        }

        //目标串中的每个元素都在原串中，且对应个数相同
        return true;
    }

    public static void main(String[] args) {
        JSONArray a1 = new JSONArray();
        JSONObject ao1 = new JSONObject();
        ao1.put("inforName","姓名");
        ao1.put("infoType",1);
        ao1.put("cartType",null);
        a1.add(ao1);
        JSONObject ao2 = new JSONObject();
        ao2.put("inforName","学生证/学籍卡照片");
        ao2.put("inforValue","484366");
        ao2.put("infoType",2);
        a1.add(ao2);

        JSONArray b1 = new JSONArray();
        JSONObject bo1 = new JSONObject();
        bo1.put("infoType",1);
        bo1.put("inforName","姓名");
        b1.add(bo1);
        JSONObject bo2 = new JSONObject();
        bo2.put("infoType",2);
        bo2.put("inforValue","484366");
        bo2.put("inforName","学生证/学籍卡照片");
        b1.add(bo2);

        System.out.println(a1.toString());
        System.out.println(b1.toString());
        System.out.println(isScrambledString(a1.toString(),b1.toString()));

        String a = "[[{\"inforName\":\"姓名\",\"infoType\":1},{\"inforName\":\"学生证/学籍卡照片\",\"inforValue\":\"484366\",\"infoType\":2}]]";
        String b = "[[{\"infoType\":1,\"inforName\":\"姓名\"},{\"infoType\":2,\"inforValue\":\"484366\",\"inforName\":\"学生证/学籍卡照片\"}]]";
        boolean result = isScrambledString(a,b);
        System.out.println(result);

        System.out.println(ao1);
        for(String key:ao1.keySet()){
            System.out.println("=======================key==================="+key);
            String valueStr=ao1.get(key).toString();

        }
    }
}
