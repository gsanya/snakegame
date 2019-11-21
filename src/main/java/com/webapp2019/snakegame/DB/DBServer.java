package com.webapp2019.snakegame.DB;
import org.springframework.context.annotation.Bean;

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



    public boolean checkLogin(String Username, String Password){
        String password;
        try{
            Statement stat= con.createStatement();
            String sql = "select * from userdatabase.users where username=\""+Username+"\"";
            ResultSet rs=stat.executeQuery(sql);
            rs.next();
            password = rs.getString("password");
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        if(password.equals(Password))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void addUser() {
//        maxid, if user exists
//        INSERT INTO `userdatabase`.`users` (`userId`, `userName`, `password`, `bestScore`, `matches`) VALUES (maxid+1, User, Password, '0', '0');
//        try{
//            Statement stat= con.createStatement();
//            String sql = "select * from userdatabase.users where username=\""+Username+"\"";
//            ResultSet rs=stat.executeQuery(sql);
//            rs.next();
//            password = rs.getString("password");
//        } catch (SQLException e){
//            e.printStackTrace();
//            return false;
//        }
    }
}

