package cn.edu.sustech.cs209.chatting.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionCheck extends Thread {
    final int PACKET_INTERVAL = 5; // 设置定时发送数据包的时间间隔，单位为秒
    Socket currentSocket;

    public ConnectionCheck(Socket socket) {
        currentSocket = socket;
    }

    public void run() {
        try {
            OutputStream outputStream = currentSocket.getOutputStream();
            while (!currentSocket.isClosed()) {
                // 每隔 PACKET_INTERVAL 秒向客户端发送一个固定的数据包
                outputStream.write("keep alive".getBytes());
                outputStream.flush();
                Thread.sleep(PACKET_INTERVAL * 1000);
            }
        }catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}


