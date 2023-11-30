package com.example.ZMQ.DEALER_ROUTER;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;

class ClientTask extends Thread {
    private String id;

    public ClientTask(String id) {
        this.id = id;
    }

    @Override
    public void run() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.DEALER);
            socket.setIdentity(id.getBytes(ZMQ.CHARSET));
            socket.connect("tcp://localhost:5570");

            System.out.println("Client " + id + " started");
            int reqs = 0;

            Poller poller = context.createPoller(1);
            poller.register(socket, Poller.POLLIN);

            while (true) {
                reqs++;
                System.out.println("Req #" + reqs + " sent..");
                socket.send(String.format("request #%d", reqs));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }

                int poll = poller.poll(1000);
                if (poll == -1)
                    break; // Interrupted
                if (poller.pollin(0)) {
                    String msg = socket.recvStr();
                    System.out.println(id + " received: " + msg);
                }
            }
        }
    }
}

public class client_dealer_router {
    public static void main(String[] args) {
        String clientId = args[0];
        Thread client = new ClientTask(clientId);
        client.start();
    }
}
