package cn.edu.sustech.cs209.chatting.client;

import javafx.fxml.FXML;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.TimerTask;

class TimeoutCheck extends TimerTask {
    Socket socket;
    Controller controller;
    public TimeoutCheck(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            if (inputStream.available() <= 0){
                controller.ConnectionState.setStyle("-fx-text-fill: red;");
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }
}
