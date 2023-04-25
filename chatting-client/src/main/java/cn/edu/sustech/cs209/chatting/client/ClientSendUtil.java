package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.Request;
import cn.edu.sustech.cs209.chatting.common.Response;

import java.io.IOException;


public class ClientSendUtil {
  public static Response sendTextRequest(Request request) throws IOException {
    Response response = null;
    try {
      ClientInfo.oos.writeObject(request);
      ClientInfo.oos.flush();
      System.out.println("Client send request: " + request.getAction());
      if (!"exit".equals(request.getAction())) {
        response = (Response) ClientInfo.ois.readObject();
        System.out.println("Client receive response: " + response);
      } else {
        System.out.println("Client exit");
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return response;
  }

  public static void sendTextRequestPure(Request request) throws IOException {
    ClientInfo.oos.writeObject(request);
    ClientInfo.oos.flush();
  }

}

