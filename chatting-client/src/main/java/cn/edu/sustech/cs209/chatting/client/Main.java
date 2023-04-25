package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Request;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

  public static void main(String[] args) {
    launch();


  }

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
    stage.setScene(new Scene(fxmlLoader.load()));
    stage.setTitle("Chatting Client");
    stage.show();
    stage.setOnCloseRequest(e -> {
      e.consume();
      try {
        Request request = new Request();
        request.setAction("exit");
        request.setAttribute("user", ClientInfo.currentUser);
        ClientSendUtil.sendTextRequestPure(request);

      } catch (IOException ex) {
        ex.printStackTrace();
      }
      stage.close();
    });
  }


}
