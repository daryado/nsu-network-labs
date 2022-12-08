package main.java.ru.nsu.ccfit.darya.client;

import java.io.IOException;

public class Main {
    public static void main(String[] args){
            String ip = args[0];
            int port = Integer.parseInt(args[1]);

            TCPClientSocket client = new TCPClientSocket(port, ip);
            client.sendFile(args[2]);
    }
}
