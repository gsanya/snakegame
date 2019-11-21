package com.webapp2019.snakegame.gameServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class SnakeServer {
    private static DatagramSocket socket;
    private static boolean running;
    //arraylist: változtatható méretű tömb a kliensekne
    private static ArrayList<ClientInfo> clients = new ArrayList <ClientInfo>();
    private static int clientId;

    public static void start (int port) {
        try {
            socket = new DatagramSocket(port);
            running=true;
            listen();

            System.out.println("Server started on port: " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //üzenet mindenkinek
    private static void broadcast(String message) {
        for (ClientInfo info: clients) {
            send(message, info.getAddress(), info.getPort());
        }
    }

    //üzenet 1 címzettnek
    private static void send(String message, InetAddress address, int port) {
        try{
            //üzenet: stringből byte, ezt küldjük át
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket packet= new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            System.out.println("Message sent to: "+address.getHostAddress()+", port:" + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //üzenetek fogadása
    private static void listen(){

        //külön thread kell a beérkező adatok fogadására
        Thread listenThread = new Thread("Listener") {
            public void run() {
                try {
                    while(running) {
                        //beérkező adat beolvasása, 1024 byte asszem elég lesz
                        byte[] data = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        socket.receive(packet);

                        //byteból stringet csinálunk, a fölösleges (üres) végét levágjuk
                        String message = new String (data);
                        message = message.substring(0, message.indexOf("\\e"));

                        //beérkező adat kezelése, ha nem parancs akkor küldkük mindenkinek
                        if(!isCommand(message, packet)) {
                            broadcast(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }; listenThread.start();
    }
    /*
    szerver parancsok (majd lesz több)
    \con:[name] csatlakozás a szerverre

     */

    //eldöntjük hogy a beérkező üzenet parancs-e
    private static boolean isCommand(String message, DatagramPacket packet) {
        if (message.startsWith("\\con:")) {
            //connection: kliens adatait berkajuk a clientinfo listába
            //futásidőben Id (lehet nem fog kelleni) mindig 1-gyel nő (így nem lesz ismétlődés)
            String name = message. substring(message.indexOf(":")+1);
            //elküldjük eddig ki online
            send(getOnlineUserList(), packet.getAddress(), packet.getPort());
            //hozzáadjuk az új klienst
            clients.add(new ClientInfo(name, clientId++, packet.getAddress(), packet.getPort()));
            //elküldjük hogy valaki belogolt
            broadcast("\\con:" + name);
            broadcast("User " + name + " connected!");

            return true;
        }
        return false;
    }

    //összeszedjük ki online
    private static String getOnlineUserList() {
        String userList= "\\list:";
        for (ClientInfo info: clients) {
            userList+=info.getName() + "#";
        }

        return userList;
    }


    //szerver leállítás
    public static void stop () {
        running=false;
    }
}
