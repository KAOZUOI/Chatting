package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.*;
import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ClientThread extends Thread {
  Controller controller;

  public void run() {

    while (ClientInfo.clientSocket.isConnected()) {
      try {
        System.out.println("Client receive response");
        Response response = (Response) ClientInfo.ois.readObject();
        ResponseType type = response.getType();
        if (type == ResponseType.LOGIN) {
          User newUser = (User) response.getData("loginUser");
          ClientInfo.onlineUsers.add(newUser.getNickname());
        } else if (type == ResponseType.GETUSERLIST) {
          List<String> updateUserList = (List<String>) response.getData("userList");
          ClientInfo.onlineUsers = updateUserList;
        } else if (type == ResponseType.GETHISTORYMESSAGE) {
          List<Message> historyMessageList = (List<Message>) response.getData("historyMessage");
          ClientInfo.userMessageList = historyMessageList;
        } else if (type == ResponseType.LOGOUT) {
          // update onlineUsers in ClientInfo
          User logoutUser = (User) response.getData("logoutUser");
          ClientInfo.onlineUsers.remove(logoutUser.getNickname());
        } else if (type == ResponseType.SENDMESSAGE) {
          Message msg = (Message) response.getData("message");
          System.out.println(msg.getMessage());
          ClientInfo.userMessageList.add(msg);

          // update chatContentList in Controller
          updateChatContentList(msg);
        } else if (type == ResponseType.SENDGROUPMESSAGE) {
          Message msg = (Message) response.getData("message");
          System.out.println(msg.getMessage());
          ClientInfo.userMessageList.add(msg);
          // update chatContentList in Controller
          updateChatContentList(msg);
        } else if (type == ResponseType.SENDFILE) {
          getSendFile(response);
        } else if (type == ResponseType.READYTORECEIVEFILE) {
          sendFile(response);
        } else if (type == ResponseType.RECEIVEFILE) {
          receiveFile(response);
        }
      } catch (IOException | ClassNotFoundException e) {
        System.out.println("Client receive response error");
        Platform.runLater(() -> {
          controller.ConnectionState.setStyle("-fx-text-fill: red;");
        });
        e.printStackTrace();
        stop();
      }

    }


  }

  private void getSendFile(Response response) throws IOException {
    FileInfo getFile = (FileInfo) response.getData("sendFile");
    Request request = new Request();
    request.setAction("getFile");
    request.setAttribute("sendFile", getFile);

    getFile.setDestName("D:\\FileTransferDestination\\file.txt");
    getFile.setDestIp(ClientInfo.HOST);
    getFile.setDestPort(ClientInfo.FILE_PORT);


    ClientSendUtil.sendTextRequestPure(request);

  }

  private void sendFile(Response response) {
    final FileInfo sendFile = (FileInfo) response.getData("sendFile");

    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    Socket socket = null;
    try {
      socket = new Socket(sendFile.getDestIp(), sendFile.getDestPort());
      bis = new BufferedInputStream(new FileInputStream(sendFile.getPathName()));
      bos = new BufferedOutputStream(socket.getOutputStream());

      byte[] buffer = new byte[1024];
      int n = -1;
      while ((n = bis.read(buffer)) != -1) {
        bos.write(buffer, 0, n);
      }
      bos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtil.close(bis, bos);
      SocketUtil.close(socket);
    }
  }

  private void receiveFile(Response response) {
    final FileInfo sendFile = (FileInfo) response.getData("sendFile");
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    ServerSocket serverSocket = null;
    Socket socket = null;
    try {
      serverSocket = new ServerSocket(sendFile.getDestPort());
      socket = serverSocket.accept();
      bis = new BufferedInputStream(socket.getInputStream());
      bos = new BufferedOutputStream(new FileOutputStream(sendFile.getDestName()));
      byte[] buffer = new byte[1024];
      int n = -1;
      while ((n = bis.read(buffer)) != -1) {
        bos.write(buffer, 0, n);
      }
      bos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtil.close(bis, bos);
      SocketUtil.close(socket);
      SocketUtil.close(serverSocket);
    }
  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  public void updateChatContentList(Message message) {
    Platform.runLater(() -> {
      controller.chatContentList.getItems().add(message);
    });
  }
}
