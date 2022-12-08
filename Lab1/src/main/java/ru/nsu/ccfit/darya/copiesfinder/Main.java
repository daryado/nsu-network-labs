package main.java.ru.nsu.ccfit.darya.copiesfinder;

import java.io.IOException;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args){
        try{
            String ip = (args[0]);
            int port = Integer.parseInt(args[1]);

            InetAddress group = InetAddress.getByName(ip);
            PrintableAddressMap addressMap = new PrintableAddressMap();
            MulticastSend send = new MulticastSend(addressMap, group, port);
            send.start();

            MulticastReceiver receive = new MulticastReceiver(addressMap, group, port);
            receive.start();

            Finder finder = new Finder(addressMap);
            finder.start();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
