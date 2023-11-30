package com.example.ZMQ.DEALER_ROUTER;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.ArrayList;
import java.util.List;

class ServerTask3 extends Thread {
    private int numServer;

    public ServerTask3(int numServer) {
        this.numServer = numServer;
    }

    @Override
    public void run() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket frontend = context.createSocket(SocketType.ROUTER);
            frontend.bind("tcp://*:5570");

            ZMQ.Socket backend = context.createSocket(SocketType.DEALER);
            backend.bind("inproc://backend");

            List<ServerWorker> workers = new ArrayList<>();
            for (int i = 0; i < numServer; i++) {
                ServerWorker worker = new ServerWorker(context, i);
                worker.start();
                workers.add(worker);
            }

            ZMQ.proxy(frontend, backend, null);
        }
    }
}

class ServerWorker extends Thread {
    private ZContext context;
    private int id;

    public ServerWorker(ZContext context, int id) {
        this.context = context;
        this.id = id;
    }

    @Override
    public void run() {
        ZMQ.Socket worker = context.createSocket(SocketType.DEALER);
        worker.connect("inproc://backend");
        System.out.println("Worker#" + id + " started");

        while (!Thread.currentThread().isInterrupted()) {
            ZMsg msg = ZMsg.recvMsg(worker);
            if (msg == null)
                break;
            System.out.println("Worker#" + id + " received " + msg.toString());
            msg.send(worker);
        }
        worker.close();
    }
}

public class server_dealer_router {
    public static void main(String[] args) {
        int numServer = Integer.parseInt(args[0]);
        Thread server = new ServerTask3(numServer);
        server.start();
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
