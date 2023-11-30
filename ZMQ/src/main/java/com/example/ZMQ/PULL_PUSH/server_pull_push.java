package com.example.ZMQ.PULL_PUSH;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class server_pull_push {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()){
            ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:5557");
            ZMQ.Socket collector = context.createSocket(SocketType.PULL);
            collector.bind("tcp://*:5558");

            while(true){
                String message = collector.recvStr();
                System.out.println("Server: publishing update "+message);
                publisher.send(message);
            }
        }
    }
}
