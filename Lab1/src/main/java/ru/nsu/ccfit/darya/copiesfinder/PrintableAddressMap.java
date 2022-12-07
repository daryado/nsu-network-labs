package main.java.ru.nsu.ccfit.darya.copiesfinder;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PrintableAddressMap {
    private final HashMap<SocketAddress, Long> addressMap;
    long timeout = 5000;

    public PrintableAddressMap() {
        addressMap = new HashMap<>();
    }

    public void add(SocketAddress address, long time) {
        if (addressMap.containsKey(address)) {
            addressMap.replace(address, time);
            return;
        }
        addressMap.put(address, time);
        print();
    }

    public void cleaner() {
        boolean toPrint = false;
        Iterator<Map.Entry<SocketAddress, Long>> iterator = addressMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<SocketAddress, Long> entry = iterator.next();
            if (System.currentTimeMillis() - entry.getValue() > timeout) {
                iterator.remove();
                toPrint = true;
            }
        }
        if (toPrint) {
            print();
        }
    }

    public void print() {
        for (SocketAddress socketAddress : addressMap.keySet()) {
            System.out.println(socketAddress);
        }
        System.out.println("--------");
    }
}
