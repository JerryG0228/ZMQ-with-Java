package com.example.ZMQ.REQ_REP;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class client_req_rep {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()){
            System.out.println("Connecting to hello world server...");
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");

            for (int req = 0; req < 10; req++) {
                System.out.println("Sending request "+ req +" ...");
                socket.send("Hello");

                byte[] message = socket.recv(0);
                System.out.println("Received reply " +req+" [ "+new String(message,ZMQ.CHARSET)+" ]");
            }
        }
    }
}
