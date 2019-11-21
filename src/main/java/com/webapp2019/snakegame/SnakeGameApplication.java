package com.webapp2019.snakegame;

import com.webapp2019.snakegame.DB.DBServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
public class SnakeGameApplication {
    public static DBServer db_server;
    public static void main(String[] args) {
        System.out.println("DB load");
        //DBserver is only used when querrying infromation, and when information is added
        db_server =new DBServer();
        //TODO:
        //I should have a
        //This part handles the http requests
        SpringApplication.run(SnakeGameApplication.class, args);
        System.out.println("other thread");
    }

}
