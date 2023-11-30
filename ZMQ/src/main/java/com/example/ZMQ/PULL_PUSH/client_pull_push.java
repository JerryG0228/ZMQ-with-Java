package com.example.ZMQ.PULL_PUSH;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class client_pull_push {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()){
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);
            subscriber.connect("tcp://localhost:5557");
            ZMQ.Socket publisher = context.createSocket(SocketType.PUSH);
            publisher.connect("tcp://localhost:5558");

            ZMQ.Poller poller = context.createPoller(1);
            poller.register(subscriber, ZMQ.Poller.POLLIN);

            Random random = new Random();
            while(true){
                poller.poll(100);
                if(poller.pollin(0)){
                    String message = subscriber.recvStr();
                    System.out.println("I: received message "+message);
                }else{
                    int randomNum = random.nextInt(99)+1;
                    if (randomNum<10){
                        publisher.send(String.format("%d",randomNum).getBytes(ZMQ.CHARSET));
                        System.out.println("I: sending message "+randomNum);
                    }
                }
            }
        }
    }
}
