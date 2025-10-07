package com.centit.pay.utils;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/11/21 18:25
 * @description ：
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

public class DBUtil {
    //连接池对象
    private static BasicDataSource ds;
    //加载参数
    static{
        Properties p = new Properties();
        try {
//            p.load(DBUtil.class.getClassLoader().getResourceAsStream("db.properties"));
//            String driver = p.getProperty("driver");
//            String url = p.getProperty("url");
//            String user = p.getProperty("user");
//            String pwd = p.getProperty("pwd");
//            String initSize = p.getProperty("initSize");
//            String maxSize = p.getProperty("maxSize");

            String driver = "oracle.jdbc.driver.OracleDriver";
            String url = "jdbc:oracle:thin:@193.168.1.165:1521/wwsjzx";
            String user = "gzcx";
            String pwd = "gzcx";
            String initSize = "5";
            //创建连接池
            ds = new BasicDataSource();
            //设置参数
            ds.setDriverClassName(driver);
            ds.setUrl(url);
            ds.setUsername(user);
            ds.setPassword(pwd);
            ds.setInitialSize(new Integer(initSize));
            ds.setMaxIdle(10);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("加载配置文件失败",e);
        }
    }
    /*
     * 以上就是将配置文件里的参数全部读取出来，接下来就是要
     * 写两个方法，一个是用来创建连接的，一个关闭连接
     * */
    public static Connection getConnection() throws SQLException{
        return ds.getConnection();
    }

    public static void close(Connection conn){
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭连接失败",e);
            }
        }
    }
}