package com.webapp2019.snakegame.database;

import java.sql.*;

public class DataBaseServer {
    public Connection con;
//Comment
    public DataBaseServer() {
        String host="jdbc:mysql://localhost:3306/userdatabase";
    //server constructor
        String uName ="root";
        String uPass ="1234";
        try {
            con = DriverManager.getConnection(host, uName, uPass);
            System.out.println("Connection successfully created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    //check if Login exists
    public boolean checkLogin(String Username, String Password){
        String password;
        ResultSet rs;
        try{
            Statement stat= con.createStatement();
            String sql = "select * from userdatabase.users where username='"+Username+"';";
            rs=stat.executeQuery(sql);
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        try{
            if(!rs.next())
                return false;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        try{
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
    //Adds user
    public String addUser(String Username, String Password) {
//        maxid, if user exists
        ResultSet rs;
        if(Username.equals("")&&Password.equals(""))
            return "Please type in a username and a password!";
        if(Username.equals(""))
            return "Please type in a username!";
        if(Password.equals(""))
            return "Please type in a password!";
        //query the database for the username
        try{
            Statement stat= con.createStatement();
            String sql = "select * from userdatabase.users where username='"+Username+"';";
            rs=stat.executeQuery(sql);
        } catch (SQLException e){
            e.printStackTrace();
            return "Coudn't execute query";
        }
        //check if user already exists
        try{
            if(rs.next())   //if there is a next, than we found a user with that name, so we return
                return "Username already taken.";
        } catch (SQLException e){
            e.printStackTrace();
            return "Rs.next() exception";
        }
        //add new user with password
        try{
            Statement stat= con.createStatement();
            String sql = "INSERT INTO `userdatabase`.`users` (`userName`, `password`, `bestScore`, `matches`) VALUES ('"+Username+"', '"+Password+"', '0', '0');";
            stat.executeUpdate(sql);
        } catch (SQLException e){
            e.printStackTrace();
            return "Couldn't execute update.";
        }
        return "User added.";
    }
    //sets bestscore of user
    public boolean setBestScore(String Username, int Score){
        try{
            Statement stat= con.createStatement();
            String sql ="UPDATE userdatabase.users SET bestscore='"+Score+"'WHERE userName = '"+Username+"';";
            stat.executeUpdate(sql);
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //increments matches of user
    public boolean incMatches(String Username){
        try{
            Statement stat= con.createStatement();
            String sql ="UPDATE userdatabase.users SET matches=matches+1 WHERE userName = '"+Username+"';";
            stat.executeUpdate(sql);
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

