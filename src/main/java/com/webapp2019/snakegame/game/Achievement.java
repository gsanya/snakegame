package com.webapp2019.snakegame.game;

public class Achievement {
    Point coord;

    Achievement(Point coord){
        this.coord=coord;
    }

    public Point getCoord(){
        return coord;
    }


    public int getX(){return coord.getX();}

    public int getY(){return  coord.getY();}

}
