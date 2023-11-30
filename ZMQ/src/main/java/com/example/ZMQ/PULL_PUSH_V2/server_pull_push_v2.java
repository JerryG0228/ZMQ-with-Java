package com.example.ZMQ.PULL_PUSH_V2;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class server_pull_push_v2 {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()){
            ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:5557");
            ZMQ.Socket collector = context.createSocket(SocketType.PULL);
            collector.bind("tcp://*:5558");

            while(true){
                String message = collector.recvStr();
                System.out.println("server: publishing update => "+ message);
                publisher.send(message.getBytes(ZMQ.CHARSET));
            }
        }
    }
}
