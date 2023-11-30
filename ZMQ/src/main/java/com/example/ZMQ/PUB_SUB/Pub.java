package com.example.ZMQ.PUB_SUB;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.SocketType;

import java.util.Random;

public class Pub {
    public static void main(String[] args) throws Exception{
        System.out.println("Publishing updates at weather server...");

        try(ZContext context = new ZContext()){
            ZMQ.Socket pub = context.createSocket(SocketType.PUB);
            pub.bind("tcp://*:5556");

            Random random = new Random();

            while(true){
                int zipcode = random.nextInt(99999)+1;
                int temporature = random.nextInt(215)-80;
                int relhumidity = random.nextInt(50)+10;

                String info = String.format("%d %d %d",zipcode,temporature,relhumidity);
                pub.send(info.getBytes(ZMQ.CHARSET),0);
            }
        }
    }
}
