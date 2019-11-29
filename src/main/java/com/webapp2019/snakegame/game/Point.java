package com.webapp2019.snakegame.game;

import java.util.Random;

//Basic point class with some basic functions
//Point represents a part of the snake
public class Point {
    private int x = 0;
    private int y = 0;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Point){
            return x==((Point) obj).getX() && y==((Point) obj).getY();
        }
        return super.equals(obj);
    }

    void move(eDirection dir){
        switch (dir)//fej koordináták
        {
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
            case UP:
                y--;
                break;
            case DOWN:
                y++;
                break;
            default:
                break;
        }

    }

    boolean outOfBorder(){
        return x >= Game.MAP_SIZE_X || x < 0 || y >= Game.MAP_SIZE_Y || y < 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
