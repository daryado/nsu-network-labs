package main.java.ru.nsu.ccfit.darya.copiesfinder;

import java.io.IOException;
import java.net.*;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf;

    InetAddress group;
    int port;
    PrintableAddressMap addressMap;

    public MulticastReceiver(PrintableAddressMap addressMap, InetAddress group, int port) throws IOException {
        socket = new MulticastSocket(port);
        buf = new byte[1024];

        this.group = group;
        this.port = port;
        this.addressMap = addressMap;

        socket.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByInetAddress(group));
    }

    @Override
    public void run() {
        while(true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                addressMap.add(packet.getSocketAddress(), System.currentTimeMillis());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}