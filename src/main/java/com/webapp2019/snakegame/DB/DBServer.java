package com.webapp2019.snakegame.DB;
import java.sql.*;

public class DBServer {
    public Connection con;
    public DBServer() {
        String host="jdbc:mysql://localhost:3306/world";
        String uName ="root";
        String uPass ="1234";
        try {
            con = DriverManager.getConnection(host, uName, uPass);
            System.out.println("Connection successfully created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void LoadData() {

    }
}

