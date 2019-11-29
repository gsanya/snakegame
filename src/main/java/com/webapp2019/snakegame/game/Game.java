package com.webapp2019.snakegame.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.webapp2019.snakegame.SnakeGameApplication;
import javafx.scene.paint.Color;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Game {

    public static final int MAP_SIZE_X = 50;
    public static final int MAP_SIZE_Y = 35;
    public static final int GAME_SPEED = 110;
    private static Game instance = new Game();

    private GameStateEnum gameState;
    private List<Point> freeCoords;
    public static boolean STOP = false;
    public long timeInMillis;

    //every connection starts as watcher, than when play is pressed, it will be a player
    private static Map<WebSocket, Player> players = new ConcurrentHashMap<>();
    private static List<WebSocket> watchers = new ArrayList<>();

    //this is the "apple" in the game
    private Achievement achievement;

    //ready players
    public int numOfActivePlayers;

    private Thread thread;

    //empty constructor
    private Game() {
    }

    //initializer function
    public void init() {
        players = new ConcurrentHashMap<>();
        thread = new Thread(new GameThread());
        thread.start();
    }

    //checks if 2 colors are the same whitin a treshold
    private boolean sameColor(String color1, String color2,int threshold){
        if(Integer.parseInt(color1.substring(1,3),16)/threshold!=Integer.parseInt(color2.substring(1,3),16)/threshold){
            return false;
        }
        if(Integer.parseInt(color1.substring(3,5),16)/threshold!=Integer.parseInt(color2.substring(3,5),16)/threshold){
            return false;
        }
        if(Integer.parseInt(color1.substring(5,7),16)/threshold!=Integer.parseInt(color2.substring(5,7),16)/threshold){
            return false;
        }
        return true;
    }

    //random color in string
    private String randomColor(){
        Random random = new Random();
        Color color = new Color(random.nextDouble(),random.nextDouble(), random.nextDouble(), 1.0);
        return "#"+color.toString().substring(2);
    }

    //adds new player according to json (name and color)
    //If color is the same within a threshold as another player, it will get a random color
    synchronized public void addNewPlayer(WebSocket connection, String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String name = jsonObject.get("name").getAsString();
        String color = jsonObject.get("color").getAsString();


        boolean hasSameColor=false;
        do {
            hasSameColor=false;
            if(sameColor(color,"#"+ Color.RED.toString().substring(2),10)){
                color=randomColor();
            }
            if(sameColor(color,"#FFFFFF",10)){
                color=randomColor();
            }
            for (Player player:players.values()){
                if(sameColor(color,player.getColor(),10)){
                    color=randomColor();
                    hasSameColor=true;
                    break;
                }
            }
        }while (hasSameColor);

        Player player = new Player(getRandFreeCoord(), connection.hashCode(), color, name);
        players.put(connection, player);
        if(!watchers.contains(connection)){
            watchers.add(connection);
        }
    }

    //reinitializes player for the next game
    synchronized public void reAddNewPlayer(WebSocket connection) {
        Player player=players.get(connection);
        player.reinitPlayer(getRandFreeCoord(), connection.hashCode());
        player.setReady(true);
        players.put(connection,player);
        numOfActivePlayers++;
    }

    //adds new watcher
    public void addWatcher(WebSocket connection) {
        watchers.add(connection);
    }

    //removes watcher
    public void removeWatcher(WebSocket connection) {
        watchers.remove(connection);
    }

    //we remove the player, and all the ids above are decremented
    synchronized public void removePlayer(WebSocket connection) {
        int playerId=players.get(connection).getId();
        for(Player player:players.values()){
            if(player.getId()>playerId){
                player.decreaseId();
            }
        }
        //only decrement active players if removed player wasn't dead
        if(!players.get(connection).isGameOver()){
            numOfActivePlayers--;
        }
        players.remove(connection);
    }



    //prints the killed player to console
    private void printDiedPlayer(Player player){
        System.out.println("Player "+player.getId()+" is died, more " + (numOfActivePlayers-1));
    }

    //kills all the players
    private void killPlayers(){
        for(Player player:players.values()) {
            player.killPlayer();
        }
    }

    //returns true, if ready to play
    synchronized private boolean readyToPlay() {
        if(numOfActivePlayers>1) {
            for (Player player : players.values()) {
                if (!player.isReady()) {
                    return false;
                }
            }
        }
        else
            return false;
        return true;
    }

    //sends the gamestate to the clients
    private void refreshGUI() {
        JsonObject json = new JsonObject();
        json.add("players", new Gson().toJsonTree(players.values()));
        json.add("achievements", new Gson().toJsonTree(achievement));
        String jsonString = new Gson().toJson(json);
        for (WebSocket watcher : watchers) {
            try {
                watcher.send(jsonString); // ezt szepen kéne megoldani amikor vége a jáatékna a watchernél meghal mert connection closed van
            }catch (WebsocketNotConnectedException e){
                e.printStackTrace();
            }
        }
    }

    //removes a point from the free coords
    public void removeFreeCoord(Point point) {
        freeCoords.remove(point);
    }

    //adds a point to the free coords
    public void addFreeCoord(Point point) {
        freeCoords.add(point);
    }

    //generates a random coordinate from the free coords
    public Point getRandFreeCoord() {
        Random rand = new Random();
        int index = rand.nextInt(freeCoords.size());
        Point point = freeCoords.get(index);
        freeCoords.remove(index);
        return point;
    }


    //basic getters
    public static Game getInstance() {
        return instance;
    }

    public Player getPlayer(WebSocket connection) {
        return players.get(connection);
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public GameStateEnum getGameState() {
        return gameState;
    }

    public int getNumOfPlayers() {
        return players.size();
    }


    //basic setters
    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    //gameThread class (main game logic method is here)
    public class GameThread extends Thread {

        private int tmpNumOfActivePlayers;

        //game initializer
        private void initThread(){
            killPlayers();
            numOfActivePlayers=0;
            tmpNumOfActivePlayers =0;
            timeInMillis=System.currentTimeMillis();
            freeCoords = new ArrayList<>();
            for (int i = 0; i < MAP_SIZE_X; i++) {
                for (int j = 0; j < MAP_SIZE_Y; j++) {
                    freeCoords.add(new Point(i, j));
                }
            }

            achievement = new Achievement(getRandFreeCoord());
            gameState = GameStateEnum.WAITING_FOR_PLAYERS;
            System.out.println("New Game - Waiting for players");
        }

        //moves the players to their next location
        private void movePlayers() {
            for (Player player : players.values()) {
                if(!player.isGameOver()) {
                    player.move();
                    if(player.isGameOver()){
                        printDiedPlayer(player);
                        numOfActivePlayers--;
                    }
                }
            }
        }

        //main game logic
        @Override
        public void run() {
            while (!STOP) {
                initThread();
                //while not ready to play, or only one player, we sleep, and check status every 1000 seconds
                while (!readyToPlay() || players.size() < 2) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    refreshGUI();
                }
                //Game has started
                gameState = GameStateEnum.PLAYING;
                System.out.println("Game is starting");
                refreshGUI();
                //game runs till only 1 player
                while (numOfActivePlayers > 1) {
                    tmpNumOfActivePlayers =numOfActivePlayers;
                    //it calculates the direction of the players
                    for (Player player : players.values()) {
                        if (!player.isGameOver()) {
                            player.stepPlayer();
                        }
                    }
                    //we move the players in the calculated directions
                    movePlayers();
                    //we check for collisions
                    for (Player player1 : players.values()) {
                        if (!player1.isGameOver()) {
                            for (Player player2 : players.values()) {
                                if (!player2.isGameOver()) {
                                    if (player1.getId() == player2.getId()) {
                                        continue;
                                    }
                                    player2.collisionDetection(player1);
                                    if (player2.isGameOver()) {
                                        printDiedPlayer(player1);
                                        numOfActivePlayers--;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    //the survivors each get one point for each dead player
                    if(tmpNumOfActivePlayers>numOfActivePlayers){
                        for(Player player:players.values()){
                            if(!player.isGameOver()){
                                player.increaseScore(tmpNumOfActivePlayers-numOfActivePlayers);
                            }
                        }
                    }

                    refreshGUI();
                    try {
                        Thread.sleep(GAME_SPEED);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //game has ended
                gameState = GameStateEnum.ENDING;
                System.out.println("Game is ended");
                refreshGUI();
                //save bestscore and increment matches
                for(Player player:players.values())
                {
                    //if a guest won it doesn't really matter
                    if(!player.getName().equals("GUEST"))
                    {
                        SnakeGameApplication.db_server.incMatches(player.getName());
                        if(SnakeGameApplication.db_server.getBestScore(player.getName())<player.getScore())
                        {
                            if(SnakeGameApplication.db_server.getBestScore(player.getName())==-1) {
                                System.out.println("Can't save score to database, something went wrong.");
                            }
                            else{
                                SnakeGameApplication.db_server.setBestScore(player.getName(),player.getScore());
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //controller.endingGame();
            thread.stop();
        }
    }
}
