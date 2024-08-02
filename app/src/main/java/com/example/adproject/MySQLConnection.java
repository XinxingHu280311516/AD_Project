package com.example.adproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQLConnection {
    private static final String DB_URL = "jdbc:mysql://database-connection.ciad6jbp4plx.us-east-1.rds.amazonaws.com:3306/adproject";
    private static final String USER = "admin";
    private static final String PASS = "ChenYuliang";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;

        try {
            // 注册 JDBC 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 打开连接
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 执行查询
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT username FROM Users";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集
            while (rs.next()) {
                // 通过字段检索
                int id  = rs.getInt("username");


                // 输出数据
                System.out.print("Username: " + id);
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            // 处理 JDBC 错误
            e.printStackTrace();
        } finally {
            // 最后块用于关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (Exception se2) {
            } // 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (Exception se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}
