package cn.edu.sustech.cs209.chatting.server;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {

        int port = 52209;
        //initialize server socket
        ServerSocket serverSocket = new ServerSocket(port);
        //new Thread to listen to client
        new Thread(() -> {
            try {
                while (true) {
                    // listen to client connection
                    Socket socket = serverSocket.accept();
                    System.out.println("Client Connecting"
                        + socket.getInetAddress().getHostAddress()
                        + ":" + socket.getPort());

                    //create Thread for each client,
                    //invoke RequestProcessor to process every client's request in Thread
                    new Thread(new RequestProcessor(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }
}

