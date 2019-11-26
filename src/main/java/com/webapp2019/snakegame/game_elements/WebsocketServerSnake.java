//It is a websocket, which works with the javascript
//https://stackoverflow.com/questions/41470482/java-server-javascript-client-websockets
//<dependency>
//    <groupId>org.java-websocket</groupId>
//    <artifactId>Java-WebSocket</artifactId>
//    <version>1.3.0</version>
//</dependency>

//it should be a state machine (waiting; starting; playing; end)

package com.webapp2019.snakegame.game_elements;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class WebsocketServerSnake extends WebSocketServer {

    private static int TCP_PORT = 4444;
    private static Game game;

    private Set<WebSocket> connections;

    public WebsocketServerSnake() {
        super(new InetSocketAddress(TCP_PORT));
        connections = new HashSet<>();
        game=Game.getInstance();
        game.init();
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        connections.add(connection);
        System.out.println("New connection from " + connection.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        Player player = game.getPlayer(connection);
        if (player == null) {
            game.removeWatcher(connection);
        } else {
            game.removePlayer(connection);
        }

        System.out.println("SomeOne Disconnected. Connections: " + game.getNumOfPlayers());

        connections.remove(connection);
        try{
            System.out.println("Closed connection");
        }
        catch (WebsocketNotConnectedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket connection, String message) {
        try{
            System.out.println(connection.getRemoteSocketAddress() + ": " + message);
        }
        catch (WebsocketNotConnectedException e){
            e.printStackTrace();
        }
        for (WebSocket sock : connections) {
            if(sock.equals(connection))
            {
                try {
                    sock.send("Echo: "+message);
                }
                catch (WebsocketNotConnectedException e){
                    e.printStackTrace();
                }

            }
            else
            {
                try{
                    sock.send("New connection: " + connection.getRemoteSocketAddress());
                }
                catch (WebsocketNotConnectedException e){
                    e.printStackTrace();
                }
            }
        }


        //if the value of the players map is null, then adds the message, otherwise do nothing - first message must be the player name
        //players.putIfAbsent(connection, message);
        switch (message) {
            case "GUEST":
                game.addWatcher(connection);
                System.out.println("New Watcher");
                return;
            case "KILLAPPLICATION":
                Game.STOP =true;
                return;
            case "RESTARTFUN":
                //restartServer();
                return;
            case "LEAVE":
                game.removePlayer(connection);
                return;
            default:break;
        }
        //if(message.charAt(0)!='{'){return;}


        Player player = game.getPlayer(connection);

        if (player == null) {
            if (game.getGameState() == GameStateEnum.WAITING_FOR_PLAYERS) {

                System.out.println(connection.hashCode()+" connected. Connections: "
                        + (Game.getInstance().numOfActivePlayers+1));
                game.addNewPlayer(connection,message);


//                connection.send("Welcome to the Arena!\n"
//                        + "Total No. of racers: " + game.getNumOfPlayers() + ".");

                connection.send(String.valueOf(connection.hashCode()));

            } else {
                connection.send("Your game is ended!");
            }

        } else {
            if (game.getGameState() == GameStateEnum.WAITING_FOR_PLAYERS && message.equals("READY")) {
                if(player.isGameOver()) {
                    game.readdNewPlayer(connection);
                }else if(!player.isReady()){
                    Game.getInstance().numOfActivePlayers++;
                }
                player.setReady(true);
                System.out.println("Player " + player.getId() + " is ready!");
            } else if (!message.equals("READY")){
                //System.out.println(player.getId() + ": " + message/*+" time:"+ System.currentTimeMillis()%10000*/);
                //Game.getInstance().addKeyCode(message);
                player.setKeyCode(message);
                //}
                //connection.send("The server has received the following message:"+ message);
            }
        }
    }

    @Override
    public void onError(WebSocket connection, Exception ex) {
        //ex.printStackTrace();
        if (connection != null) {
            connections.remove(connection);
            // do some thing if required
        }
        try{
            System.out.println("ERROR");
        }
        catch (WebsocketNotConnectedException e){
            e.printStackTrace();
        }
    }
}