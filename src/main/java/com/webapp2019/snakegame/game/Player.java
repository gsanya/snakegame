package com.webapp2019.snakegame.game;

import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.*;


public class Player {
    //we can
    private int id;
    //it is not used
    private String name;

    private boolean gameOver;

    private volatile boolean ready;
    private Point head;
    private List<Point> tail;
    private eDirection dir;
    private int score;
    private String color;
    private String keyCode;

    //constructor
    public Player(Point head, int id, String color, String playerName){
        reinitPlayer(head, id);
        this.color = color;
        this.name = playerName;
    }

    //reinitializes player
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

    //sets the direction of the player according to the keyCode
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

    //moves the player (tail and head, and check if it hit the wall or itself)
    void move() {
        Game game = Game.getInstance();
        //move the tail to the head
        tail.add(0, new Point(head.getX(), head.getY()));
        //move the head
        head.move(dir);
        //check if hit walls
        if (head.outOfBorder()) {
            killPlayer();
        }
        game.removeFreeCoord(head);
        if (game.getAchievement().getCoord().equals(head)) {
            game.setAchievement(new Achievement(game.getRandFreeCoord()));
            score++;
        } else {
            //end of tail moved
            game.addFreeCoord(tail.get(tail.size() - 1));
            tail.remove(tail.size() - 1);
        }
        //check if hit itself
        for (Point point : tail) {
            if (head.equals(point)) {
                killPlayer();

            }
        }
    }

    //checks if the player hit another player. If yes, it kills the player.
    void collisionDetection(Player other) {
        if (head.equals(other.getHead())) {
            //we shouldn't add it twice, so only one player will free up the coordinates
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

    //increments the score
    public void increaseScore(int value){
        score+=value;
    }

    //decrements id
    public void decreaseId(){this.id--;}

    //sets ready state, and sets the current time
    synchronized public void setReady(boolean ready) {
        Game.getInstance().timeInMillis=System.currentTimeMillis();
        this.ready = ready;
    }

    //kills the player
    public void killPlayer(){
        gameOver=true;
        ready=false;
    }


    //basic getters
    public String getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public int getId() {
        return id;
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


    //basic setters
    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

}
