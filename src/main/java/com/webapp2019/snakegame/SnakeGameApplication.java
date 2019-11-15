package com.webapp2019.snakegame;

import com.webapp2019.snakegame.DB.DBServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
public class SnakeGameApplication {
    public static void main(String[] args) {
        System.out.println("DB load\n");
        DBServer server=new DBServer();
        SpringApplication.run(SnakeGameApplication.class, args);
    }

}
