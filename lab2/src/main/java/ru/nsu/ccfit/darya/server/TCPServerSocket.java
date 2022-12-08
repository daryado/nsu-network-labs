package main.java.ru.nsu.ccfit.darya.server;

import java.io.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerSocket {
    private static int port;

    public TCPServerSocket(int port) throws IOException {
        int numOfConnections = 0;

        this.port = port;
        new File("uploads").mkdir();
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                ++numOfConnections;
                System.out.printf("Thread%d\n", numOfConnections);
                final Socket clientCon = server.accept();
                ConnectionThread connection = new ConnectionThread(clientCon);
                Thread clientTr = new Thread(connection);
                clientTr.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
