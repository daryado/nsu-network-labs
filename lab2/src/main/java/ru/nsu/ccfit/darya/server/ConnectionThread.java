package main.java.ru.nsu.ccfit.darya.server;

import java.io.*;
import java.net.FileNameMap;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionThread implements Runnable{

    private final String UPLOAD_DIR = "./uploads/";

    private FileOutputStream outFile;
    private final DataInputStream in;
    private final DataOutputStream out;

    private final Socket clientSocket;

    ConnectionThread(Socket client) throws IOException{
        this.clientSocket = client;
        in = new DataInputStream(client.getInputStream());
        out = new DataOutputStream(client.getOutputStream());
    }

    @Override
    public void run() {
        try {
            System.out.println("Start downloading...");
            long fileSize, start_t, end_t = 0;


            int nameSize = in.readInt();
            byte[] arrayNameBytes = new byte[nameSize];
            in.readFully(arrayNameBytes, 0, nameSize);
            String fileName = new String(arrayNameBytes, StandardCharsets.UTF_8);

            File file  = new File(UPLOAD_DIR + fileName);
            while(file.exists()){
                fileName = "1" + fileName;
                file  = new File(UPLOAD_DIR + fileName);
            }

            fileSize = in.readLong();

            outFile = new FileOutputStream(UPLOAD_DIR + fileName);

            byte[] partFileBytes = new byte[4096];
            long read = 0;
            int part, difpart = 0;

            long diftime;
            long t1, t2;

            start_t = System.currentTimeMillis();
            t1 = System.currentTimeMillis();
            while (read < fileSize) {
                part = in.read(partFileBytes);
                difpart += part;
                t2 = System.currentTimeMillis();
                diftime = t2 - t1;
                if (diftime >= 3000) {
                    System.out.println(fileName + ": Speed = " + difpart/(diftime*1000) + "Mb/sec");
                    t1 = t2;
                    difpart = 0;
                }
                outFile.write(partFileBytes, 0, part);
                read += part;
            }

            end_t = System.currentTimeMillis();
            System.out.println(fileName + ":Average speed = " + read/((end_t - start_t)*1000) + "Mb/sec");

            if (read == fileSize){
                System.out.println("SUCCESSFUL:: The size of the received data is the same");
            } else {
                System.out.println("FAILED:: The size of the received data is not the same");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try {
                outFile.close();
                out.writeInt(1);
                out.close();
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                in.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
