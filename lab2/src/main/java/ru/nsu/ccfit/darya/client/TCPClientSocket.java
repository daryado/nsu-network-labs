package main.java.ru.nsu.ccfit.darya.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPClientSocket {
    private static int port;
    private static String ip;

    public TCPClientSocket(int p, String ip) {
        this.port = p;
        this.ip = ip;
    }

    public void sendFile(String path) {
        File file = new File(path);
        String fileName = file.getName();
        long fileSize = file.length();
        byte[] nameBytes;
        byte[] fileBytes = new byte[4096];

        try (Socket clientSocket = new Socket(ip, port)) {
            DataInputStream readFromSocket = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream writeInSocket = new DataOutputStream(clientSocket.getOutputStream());
            FileInputStream readFromFile = new FileInputStream(path);
            nameBytes = fileName.getBytes(StandardCharsets.UTF_8);
            int request = 0;

            writeInSocket.writeInt(nameBytes.length);
            writeInSocket.write(nameBytes);
            writeInSocket.writeLong(fileSize);

            System.out.print("Sending file...");

            long read = 0;
            while (read < fileSize) {
                int part = readFromFile.read(fileBytes);
                writeInSocket.write(fileBytes, 0, part);
                read += part;
            }

            request = readFromSocket.readInt();
            if (request == 1) System.out.print("Send file successfully!");
            else System.out.print("Not delivered!");

            readFromFile.close();
            writeInSocket.close();
            readFromSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
