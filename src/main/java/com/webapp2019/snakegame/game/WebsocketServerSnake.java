//It is a websocket, which works with the javascript
//https://stackoverflow.com/questions/41470482/java-server-javascript-client-websockets
//<dependency>
//    <groupId>org.java-websocket</groupId>
//    <artifactId>Java-WebSocket</artifactId>
//    <version>1.3.0</version>
//</dependency>

//it should be a state machine (waiting; starting; playing; end)

package com.webapp2019.snakegame.game;

import com.webapp2019.snakegame.SnakeGameApplication;
import com.webapp2019.snakegame.model.SignIn;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebsocketServerSnake extends WebSocketServer {

    private static int TCP_PORT = 4444;
    private static Game game;

    public WebsocketServerSnake() {
        super(new InetSocketAddress(TCP_PORT));
        game=Game.getInstance();
        game.init();
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        System.out.println("New connection from " + connection.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        Player player = game.getPlayer(connection);
        if (player == null) {
            game.removeWatcher(connection);
        } else {
            game.removePlayer(connection);
            game.removeWatcher(connection);
        }

        try{
            System.out.println("SomeOne Disconnected. Connections: " + game.getNumOfPlayers());
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
        switch (message) {
            case "JOIN":
                game.addWatcher(connection);
                System.out.println("New Watcher");
                return;
            case "LEAVE":
                game.removePlayer(connection);
                return;
            default:
                break;
        }

        //delete signed out players (from logged in users, and also from player database in Game)
        if (message.contains("SIGNOUT"))
        {
            String userName=message.substring(8);
            for(SignIn user:SnakeGameApplication.users)
            {
                if(user.getUser().equals(userName))
                {
                    SnakeGameApplication.users.remove(user);
                    break;
                }
            }
            if(game.getPlayer(connection)!=null)
            {
                game.removePlayer(connection);
                game.removeWatcher(connection);
            }
            return;
        }

        Player player = game.getPlayer(connection);

        //we add the player if not yet added
        if (player == null) {
            if (game.getGameState() == GameStateEnum.WAITING_FOR_PLAYERS) {

                System.out.println(connection.hashCode()+" connected. Connections: "+ (Game.getInstance().numOfActivePlayers+1));

                game.addNewPlayer(connection,message);
                connection.send(String.valueOf(connection.hashCode()));

            } else {
                connection.send("Your game is ended!");
            }
        //if READY was sent (player has already played a game)
        } else {
            if (game.getGameState() == GameStateEnum.WAITING_FOR_PLAYERS && message.equals("READY")) {
                if(player.isGameOver()) {
                    game.reAddNewPlayer(connection);
                }else if(!player.isReady()){
                    Game.getInstance().numOfActivePlayers++;
                }
                player.setReady(true);
                System.out.println("Player " + player.getId() + " is ready!");
            //the message must be the keycode
            } else if (!message.equals("READY")){
                player.setKeyCode(message);
            }
        }
    }

    @Override
    public void onError(WebSocket connection, Exception ex) {
        //ex.printStackTrace();
        try{
            ex.printStackTrace();
            System.out.println("ERROR");
        }
        catch (WebsocketNotConnectedException e){
            e.printStackTrace();
        }
    }
}