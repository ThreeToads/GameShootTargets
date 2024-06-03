package com.example.archer.serverPart;

public class ServerApplication {
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer(4000);
    }
}
