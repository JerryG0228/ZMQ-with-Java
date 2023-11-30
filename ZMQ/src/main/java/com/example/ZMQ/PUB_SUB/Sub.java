package com.example.ZMQ.PUB_SUB;

import org.jetbrains.annotations.NotNull;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.SocketType;

public class Sub {
    public static void main(String @NotNull [] args) {
        try(ZContext context = new ZContext()){
            ZMQ.Socket sub = context.createSocket(SocketType.SUB);
            sub.connect("tcp://localhost:5556");
            System.out.println("Collecting updates from weather server...");

            String zip_filter = args.length > 1 ? args[0] : "10001";
            sub.subscribe(zip_filter.getBytes(ZMQ.CHARSET));

            int total_temp = 0;
            for (int i = 0; i < 20; i++) {
                String[] receive = sub.recvStr().split(" ");
                String zipcode = receive[0];
                int temporature = Integer.parseInt(receive[1]);
                String relhumidity = receive[2];
                total_temp += temporature;
                System.out.println(String.format("Receive temporature for zipcode '%s' was %d F",zip_filter,temporature));
            }
            System.out.println(String.format("Average temperature for zipcode '%s' was %d F",
                    zip_filter,(total_temp/20)));
        }
    }
}
