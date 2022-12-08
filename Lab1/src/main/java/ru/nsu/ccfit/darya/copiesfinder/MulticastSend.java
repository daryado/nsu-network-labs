package main.java.ru.nsu.ccfit.darya.copiesfinder;

import java.io.IOException;
import java.net.*;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSend extends Thread{
    int port;
    InetAddress group;
    MulticastSocket socket;
    PrintableAddressMap addressMap;

    String text = "hi?";
    DatagramPacket packet;
    int sleepTime = 1000;

    public MulticastSend(PrintableAddressMap addressMap, InetAddress group, int port) throws IOException {
        this.addressMap = addressMap;
        this.group = group;
        this.port = port;

        socket = new MulticastSocket();
        packet = new DatagramPacket(text.getBytes(), text.length(), group, port);
    }

    @Override
    public void run(){
          while (true) {
            try {
                socket.send(packet);
                Thread.sleep(sleepTime);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

}
