//It is a websocket, which works with the javascript

package com.webapp2019.snakegame.gameServer;

import com.sun.source.tree.CatchTree;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.http.WebSocketHandshakeException;
import java.util.HashSet;
import java.util.Set;

public class WebsocketServerSnake extends WebSocketServer {

    private static int TCP_PORT = 4444;

    private Set<WebSocket> conns;

    public WebsocketServerSnake() {
        super(new InetSocketAddress(TCP_PORT));
        conns = new HashSet<>();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conns.add(conn);
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conns.remove(conn);
        try{
            System.out.println("Closed connection");
        }
        catch (WebsocketNotConnectedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try{
            System.out.println(conn.getRemoteSocketAddress() + ": " + message);
        }
        catch (WebsocketNotConnectedException e){
            e.printStackTrace();
        }
        for (WebSocket sock : conns) {
            if(sock.equals(conn))
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
                    sock.send("New connection: " + conn.getRemoteSocketAddress());
                }
                catch (WebsocketNotConnectedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //ex.printStackTrace();
        if (conn != null) {
            conns.remove(conn);
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