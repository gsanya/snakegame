package com.webapp2019.snakegame.database;

public class User {
    private String name;
    private Integer matchNumber;
    private Integer bestScore;

    public User(String name, Integer matchNumber, Integer bestScore) {
        this.name = name;
        this.matchNumber = matchNumber;
        this.bestScore = bestScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    public Integer getBestScore() {
        return bestScore;
    }

    public void setBestScore(Integer bestScore) {
        this.bestScore = bestScore;
    }


}
