package Handlers;

import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ResolveHandler {

    private static final int MAX_REQUEST = 65535;
    private static InetSocketAddress dnsServer = ResolverConfig.getCurrentConfig().server();

    private int requestIndex = 0;
    private final LinkedList<Resolve> resolvesToSend = new LinkedList<>();
    private final HashMap<Integer, Resolve> sentResolves = new HashMap<>();

    private final SelectionKey key;
    private final DatagramChannel channel;
    private final ByteBuffer buffer = ByteBuffer.allocate(MainHandler.BUFFER_SIZE);

    public ResolveHandler(SelectionKey key) {
        this.key = key;
        channel = (DatagramChannel) key.channel();
        key.interestOps(SelectionKey.OP_READ);
        System.out.println("DNS server: " + dnsServer);
    }

    private int getNextRequestID() {
        if(requestIndex > MAX_REQUEST) {
            requestIndex = 0;
        }
        return requestIndex++;
    }

    public void addRequest(String address, ClientHandler clientHandler) {
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        resolvesToSend.add(new Resolve(address, clientHandler));
    }

    public void sendResolve() throws IOException {
        if(resolvesToSend.isEmpty()) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            return;
        }
        Resolve resolve = resolvesToSend.pop();
        int requestID = getNextRequestID();
        sentResolves.put(requestID, resolve);

        Message message = new Message();
        Header header = message.getHeader();
        header.setOpcode(Opcode.QUERY);
        header.setID(requestID);
        header.setRcode(Rcode.NOERROR);
        header.setFlag(Flags.RD);
        message.addRecord(Record.newRecord(new Name(resolve.getAddress() + "."), Type.A, DClass.IN), Section.QUESTION);

        byte[] messageData = message.toWire();
        ByteBuffer byteBuffer = ByteBuffer.wrap(messageData);
        channel.send(byteBuffer, dnsServer);
    }

    public void receiveResolve() throws IOException {
        buffer.clear();
        channel.receive(buffer);
        buffer.flip();

        Message message = new Message(buffer.array());
        int requestID = message.getHeader().getID();
        if(!sentResolves.containsKey(requestID)) {
            return;
        }
        List<Record> questions = message.getSection(Section.QUESTION);


        List<Record> answers = message.getSection(Section.ANSWER);
        if(questions.size() > 1) {
            return;
        }
        ARecord aRecord = null;
        for(Record answer : answers) {
            if(answer instanceof ARecord) {
                aRecord = (ARecord) answer;
                break;
            }
        }
        InetAddress address = (aRecord != null) ? aRecord.getAddress() : null;
        Resolve resolve = sentResolves.get(requestID);
        resolve.getClientHandler().dnsConnect(address);
        sentResolves.remove(requestID);
    }
}
