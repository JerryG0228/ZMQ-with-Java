package com.example.ZMQ.REQ_REP;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class server_req_rep {
    public static void main(String[] args) throws Exception {
        try(ZContext context = new ZContext()){
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5555");

            while(true){
                byte[] message = socket.recv(0);

                System.out.println("Received request: " + new String(message,ZMQ.CHARSET));
                Thread.sleep(1000);

                String response = "World";
                socket.send(response.getBytes(ZMQ.CHARSET),0);
            }
        }
    }
}
