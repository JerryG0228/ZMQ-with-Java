package com.example.ZMQ.DEALER_ROUTER;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;

class ClientTask2 extends Thread {
    private String id;
    private ZMQ.Socket socket;
    private ZContext context;
    private Poller poller;

    public ClientTask2(String id) {
        this.id = id;
    }

    class RecvHandler extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
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

    @Override
    public void run() {
        context = new ZContext();
        socket = context.createSocket(SocketType.DEALER);
        socket.setIdentity(id.getBytes(ZMQ.CHARSET));
        socket.connect("tcp://localhost:5570");

        System.out.println("Client " + id + " started");

        poller = context.createPoller(1);
        poller.register(socket, Poller.POLLIN);

        RecvHandler recvHandler = new RecvHandler();
        recvHandler.start();

        int reqs = 0;
        while (!Thread.currentThread().isInterrupted()) {
            reqs++;
            System.out.println("Req #" + reqs + " sent..");
            socket.send(String.format("request #%d", reqs));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

public class thread_dealer_router {
    public static void main(String[] args) {
        String clientId = args[0];
        Thread client = new ClientTask2(clientId);
        client.start();
    }
}
