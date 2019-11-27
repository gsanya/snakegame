package com.webapp2019.snakegame;

import com.webapp2019.snakegame.database.DataBaseServer;
import com.webapp2019.snakegame.game.WebsocketServerSnake;
import com.webapp2019.snakegame.model.SignIn;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.Vector;

@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
public class SnakeGameApplication {

    public static DataBaseServer db_server;
    public static Vector<SignIn> users;

    public static void main(String[] args) {
        users = new Vector<SignIn>();

        //DBserver is only used when querrying infromation, and when information is added
        db_server = new DataBaseServer();
        System.out.println("DB loaded.");

        //Run the http server (the springapplication
        SpringApplication.run(SnakeGameApplication.class, args);
        System.out.println("Springapplication is running.");

        //Run the websocket server
        new WebsocketServerSnake().start();
        System.out.println("SnakeServer started");
    }
};
