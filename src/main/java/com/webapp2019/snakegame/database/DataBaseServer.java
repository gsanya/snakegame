package com.webapp2019.snakegame.database;

import com.webapp2019.snakegame.database.User;

import java.sql.*;
import java.util.Vector;

public class DataBaseServer {
        public Connection con;

    //constructor
    public DataBaseServer(){
        String host="jdbc:mysql://localhost:3306/userdatabase";
    //server constructor
        String uName ="root";
        String uPass ="Webesalak.19";
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
        ResultSet rs;
        if(Username.equals("")&&Password.equals(""))
            return "Please type in a username and a password!";
        if(Username.equals(""))
            return "Please type in a username!";
        if(Password.equals(""))
            return "Please type in a password!";
        if(Username.contains("SIGNOUT")||Username.contains("GUEST"))
            return "Sorry, you can't use this username."; //these are control string, so it wouldn't be nice
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

    //sets bestScore of user
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

    //gets bestScore of user (return of -1 is error)
    public int getBestScore(String Username){
        ResultSet rs;
        try{
            Statement stat= con.createStatement();
            String sql = "select * from userdatabase.users where username='"+Username+"';";
            rs=stat.executeQuery(sql);
        } catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
        try{
            if(!rs.next())
                return -1;
        } catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
        try{
            return rs.getInt("bestScore");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getLeaderBoard(){
        ResultSet rs;
        Vector<User> users = new Vector<User>();

        try{
            Statement stat= con.createStatement();
            String sql = "SELECT username,bestscore, matches FROM userdatabase.users ORDER BY bestscore DESC";
            rs=stat.executeQuery(sql);
        } catch (SQLException e){
            e.printStackTrace();
            return "something went wrong";
        }
        try {
            while (rs.next())
            {
                users.add(new User(rs.getString(1),rs.getInt(3),rs.getInt(2)));
            }
        }catch (SQLException e){
            e.printStackTrace();
            return "something went wrong";
        }
        StringBuilder sb=new StringBuilder();
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td class=\"table\">");
            sb.append(user.getName());
            sb.append("</td>");
            sb.append("<td class=\"table\">");
            sb.append(user.getMatchNumber().toString());
            sb.append("</td>");
            sb.append("<td class=\"table\">");
            sb.append(user.getBestScore().toString());
            sb.append("</td>");
            sb.append("</tr>");
        }
        return sb.toString();

    }
}

