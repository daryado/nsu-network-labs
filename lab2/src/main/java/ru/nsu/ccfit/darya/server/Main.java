package main.java.ru.nsu.ccfit.darya.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try{
            int port = Integer.parseInt(args[0]);

            TCPServerSocket server = new TCPServerSocket(port);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
