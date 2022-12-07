package main.java.ru.nsu.ccfit.darya.copiesfinder;

public class Finder extends Thread{
    PrintableAddressMap addressMap;
    int sleepTime = 1000;

    public Finder(PrintableAddressMap addressMap) {
        this.addressMap = addressMap;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(sleepTime);
                addressMap.cleaner();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}