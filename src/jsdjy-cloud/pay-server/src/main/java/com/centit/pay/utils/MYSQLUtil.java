package com.centit.pay.utils;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/11/21 18:25
 * @description ：
 */

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class MYSQLUtil {
    // MySQL 8.0 以下版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/jy_test";

    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    //static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    //static final String DB_URL = "jdbc:mysql://localhost:3306/RUNOOB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";


    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "cuijian";
    static final String PASS = "root";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            stmt2 = conn.createStatement();
            String sql;

            Map<String,Integer> users = new HashMap<String,Integer>();

            String str1 ="2020-01-04 00:00:00";
            String str2 ="2020-01-05 00:00:00";

            while(!str2.equals("2020-12-06 00:00:00")){
                DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

//                String date1 = format1.format(format1.parse(str1));
//                String date2 = format1.format(format1.parse(str2));

                sql = "select DISTINCT(t1.userid) userid from t_st_login t1 " +
                        "where t1.LOGINTIME =\'"+ str1+"\'";

                System.out.println(sql);

                ResultSet rs = stmt.executeQuery(sql);
                // 展开结果集数据库
                while(rs.next()){
                    // 通过字段检索
                    String userid = rs.getString("userid");
                    if(null ==users.get(userid)){
                        users.put(userid,1);
                    }else{
                        int num = users.get(userid);
                        users.put(userid,num+1);
                    }
                }
                // 完成后关闭
                rs.close();

                sql = "select DISTINCT(t1.userid) userid from t_st_login t1 " +
                        "where t1.LOGINTIME =\'"+ str2+"\'";
                ResultSet rs2 = stmt2.executeQuery(sql);
                // 展开结果集数据库
                while(rs2.next()){
                    // 通过字段检索
                    String userid = rs2.getString("userid");
                    if(null ==users.get(userid)){
                        users.put(userid,1);
                    }else{
                        int num = users.get(userid);
                        users.put(userid,num+1);
                    }
                }
                // 完成后关闭
                rs2.close();

            Date sDate = format1.parse(str1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(sDate);
//增加7天
            cal.add(Calendar.DAY_OF_MONTH, 7);
//Calendar转为Date类型
            Date date=cal.getTime();
            str1=format1.format(date);
                str1=str1.replaceAll("12:00:00","00:00:00");

            Date sDate2 = format1.parse(str2);
            Calendar ca2 = Calendar.getInstance();
            ca2.setTime(sDate2);
//增加7天
            ca2.add(Calendar.DAY_OF_MONTH, 7);
//Calendar转为Date类型
            date=ca2.getTime();
            str2=format1.format(date);
                str2=str2.replaceAll("12:00:00","00:00:00");

                System.out.println(str2);
            }

            System.out.println(users);
            for(String key:users.keySet()){
                System.out.println(users.get(key));
            }
//            System.out.println(users);
//            for(String key:users.keySet()){
//                System.out.println(key);
//            }
            stmt.close();
            stmt2.close();
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}