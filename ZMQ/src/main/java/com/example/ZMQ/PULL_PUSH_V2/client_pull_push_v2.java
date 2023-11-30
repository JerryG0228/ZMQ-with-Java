package com.example.ZMQ.PULL_PUSH_V2;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class client_pull_push_v2 {
    public static void main(String[] args) throws InterruptedException {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);
            subscriber.connect("tcp://localhost:5557");
            ZMQ.Socket publisher = context.createSocket(SocketType.PUSH);
            publisher.connect("tcp://localhost:5558");

            ZMQ.Poller poller = context.createPoller(1);
            poller.register(subscriber, ZMQ.Poller.POLLIN);

            String clientID = args[0];
            Random random = new Random();

            while (true) {
                poller.poll(100);
                if (poller.pollin(0)) {
                    String message = subscriber.recvStr();
                    System.out.println(String.format("%s: receive status => %s", clientID, message));
                } else {
                    int randomNum = random.nextInt(99) + 1;
                    if (randomNum < 10) {
                        Thread.sleep(1000);
                        String message1 = "(" + clientID + ":ON)";
                        publisher.send(message1.getBytes(ZMQ.CHARSET));
                        System.out.println(String.format("%s: send status - activated", clientID));
                    } else if (randomNum > 90) {
                        Thread.sleep(1000);
                        String message2 = "(" + clientID + ":OFF)";
                        publisher.send(message2.getBytes(ZMQ.CHARSET));
                        System.out.println(String.format("%s: send status - deactivated", clientID));
                    }
                }
            }
        }
    }
}
