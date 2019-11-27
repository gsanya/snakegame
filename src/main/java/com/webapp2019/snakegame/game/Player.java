package com.webapp2019.snakegame.game;

import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.*;


public class Player {
    public static final KeyCode[][] KEY_CUTS = {
            {A, LEFT, J, NUMPAD4},
            {D, RIGHT, L, NUMPAD6},
            {W, UP, I, NUMPAD8},
            {S, DOWN, K, NUMPAD5}
    };

    private int id;
    private String name;

    private boolean gameOver;

    private volatile boolean ready;
    private Point head;
    private List<Point> tail;
    private eDirection dir;
    private int score;
    private String color;
    private String keyCode;
    public String getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public int getId() {
        return id;
    }

    public eDirection getDir() {
        return dir;
    }

    public Point getHead() {
        return head;
    }

    public List<Point> getTail() {
        return tail;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver() {
        this.gameOver = true;
    }

    public void killPlayer(){
        gameOver=true;
        ready=false;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    synchronized public void setReady(boolean ready) {
        Game.getInstance().timeInMillis=System.currentTimeMillis();
        this.ready = ready;
    }

    public void decreaseId(){this.id--;}

    public void increaseScore(int value){
        score+=value;
    }


    public Player(Point head, int id, String color, String playerName){
        this(head,id);
        this.color = color;
        this.name = playerName;
    }

    public void reinitPlayer(Point head, int id){
        gameOver = false;
        this.id = id;

        //head.initRand();
        this.head = head;

        tail = new ArrayList<>();
        dir = eDirection.STOP;
        keyCode=null;

        ready = false;

        score = 0;
    }

    public Player(Point head, int id) {
        reinitPlayer(head, id);
    }

    synchronized public void stepPlayer() {
        if (keyCode == null) return;
        switch (keyCode) {
            case "UP":
                if (dir == eDirection.DOWN) {
                    return;
                }
                dir = eDirection.UP;
                return;
            case "LEFT":
                if (dir == eDirection.RIGHT) {
                    return;
                }
                dir = eDirection.LEFT;
                return;
            case "DOWN":
                if (dir == eDirection.UP) {
                    return;
                }
                dir = eDirection.DOWN;
                return;
            case "RIGHT":
                if (dir == eDirection.LEFT) {
                    return;
                }
                dir = eDirection.RIGHT;
                return;
            default:
                return;
        }
    }


    void move() {
        Game game = Game.getInstance();
        tail.add(0, new Point(head.getX(), head.getY()));
        head.move(dir);
        if (head.outOfBorder()) {
            killPlayer();
        }
        game.removeFreeCoord(head);
        if (game.getAchievement().getCoord().equals(head)) {
            game.setAchievement(new Achievement(game.getRandFreeCoord()));
            score++;
        } else {
            game.addFreeCoord(tail.get(tail.size() - 1));
            tail.remove(tail.size() - 1);
        }
        for (Point point : tail) {
            if (head.equals(point)) {
                killPlayer();

            }
        }
    }

    /*void deletePlayer(Game game) {
        for (Point point : tail) {
            game.addFreeCoord(point);
        }
    }*/

    void collisionDetection(Player other) {
        if (head.equals(other.getHead())) {
            if (id < other.getId()) {
                Game.getInstance().addFreeCoord(head);
            }
            killPlayer();
            return;
        }
        for (Point point : other.getTail()) {
            if (head.equals(point)) {
                killPlayer();
                Game.getInstance().addFreeCoord(head);
                return;
            }
        }
    }


}
